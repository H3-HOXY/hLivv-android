package com.hoxy.hlivv.ui.liveqr

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.internal.Objects
import com.google.android.material.chip.Chip
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.apis.ProductControllerApi
import com.hoxy.hlivv.data.models.ProductDto
import com.hoxy.hlivv.data.models.ProductImageDto
import com.hoxy.hlivv.domain.Utils
import com.hoxy.hlivv.domain.Utils.handleApiError
import com.hoxy.hlivv.domain.Utils.showErrorDialog
import com.hoxy.hlivv.ui.component.BaseFragment
import com.hoxy.hlivv.ui.liveqr.barcode.BarcodeAnalyzer
import com.hoxy.hlivv.ui.liveqr.barcode.ProductResultFragment
import com.hoxy.hlivv.ui.liveqr.barcode.graphic.GraphicOverlay
import com.hoxy.hlivv.ui.liveqr.barcode.product.Product
import com.hoxy.hlivv.ui.liveqr.camera.WorkflowModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors


/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class LiveBarcodeScanningFragment : BaseFragment(), View.OnClickListener {

    private var preview: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var flashButton: View? = null
    private var settingsButton: View? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null
    private var cameraController: CameraControl? = null
    private val permissionsRequired = arrayOf(Manifest.permission.CAMERA)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "카메라 권한 요청이 승인되었습니다.", Toast.LENGTH_LONG).show()
                onPermissionGranted()
            } else requireActivity().supportFragmentManager.popBackStack()
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        return inflater.inflate(R.layout.fragment_live_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //requireActivity().setStatusBarTransparent()
        if (!hasPermissions(requireContext())) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            onPermissionGranted()
        }
    }

    private fun onPermissionGranted() {
        preview = requireView().findViewById(R.id.camera_preview)
        graphicOverlay =
            requireView().findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay)?.apply {
                setOnClickListener(this@LiveBarcodeScanningFragment)
            }

        promptChip = requireView().findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(
                requireContext(),
                R.animator.bottom_prompt_chip_enter
            ) as AnimatorSet).apply {
                setTarget(promptChip)
            }

        requireView().findViewById<View>(R.id.close_button)?.setOnClickListener(this)
        flashButton = requireView().findViewById<View>(R.id.flash_button)?.apply {
            setOnClickListener(this@LiveBarcodeScanningFragment)
        }
        settingsButton = requireView().findViewById<View>(R.id.settings_button)?.apply {
            setOnClickListener(this@LiveBarcodeScanningFragment)
        }

        setUpWorkflowModel()
    }

    private fun hasPermissions(context: Context) = permissionsRequired.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        workflowModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        startCamera()
        workflowModel?.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
    }


    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraProvider?.unbindAll()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.close_button -> requireActivity().supportFragmentManager.popBackStack()
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraController?.enableTorch(false)
                    } else {
                        it.isSelected = true
                        cameraController?.enableTorch(true)
                    }
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // PreviewView로 카메라 프리뷰 설정
            val previewView: PreviewView = preview ?: return@addListener
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            // 최신 이미지만 유지
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // BarcodeAnalyzer 객체 생성
            val barcodeAnalyzer = BarcodeAnalyzer(workflowModel!!, graphicOverlay!!)

            // imageAnalysis.setAnalyzer를 사용하여 BarcodeAnalyzer 등록
            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                barcodeAnalyzer.analyze(image)
            }

            // Preview와 ImageAnalysis lifecycle 바인딩
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            // graphicOverlay 설정 및 초기화
            graphicOverlay?.setCameraInfo(previewView)
            graphicOverlay?.clear()


        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
    }


    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProvider(this)[WorkflowModel::class.java]

        workflowModel!!.workflowState.observe(viewLifecycleOwner, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState

            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                WorkflowModel.WorkflowState.DETECTING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCamera()
                }

                WorkflowModel.WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCamera()
                }

                WorkflowModel.WorkflowState.SEARCHING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCamera()
                }

                WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCamera()
                }

                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation =
                wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })




        workflowModel?.detectedBarcode?.observe(viewLifecycleOwner) { barcode ->
            if (barcode != null) {
                // 바코드에서 URL 추출
                val barcodeUrl = barcode.rawValue ?: ""
                // 추출한 URL에서 id 추출
                val productId = extractIdFromUrl(barcodeUrl)
                if (productId != null) {
                    val productList = ArrayList<Product>()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val productApi = ProductControllerApi()
                            val productDto: ProductDto = productApi.getProduct1(productId)
                            val product = convertProductDtoToProduct(productId, productDto)

                            withContext(Dispatchers.Main) {
                                productList.add(product)
                                activity?.supportFragmentManager?.let { fragmentManager ->
                                    ProductResultFragment.show(
                                        fragmentManager,
                                        workflowModel!!,
                                        productList
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            handleApiError(e, findNavController(), requireContext())
//                            activity?.let {
//                                workflowModel!!.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
//                            }

                        }
                    }

                } else{
                    showErrorDialog("찾으시는 상품이  없습니다.", requireContext())
                    activity?.let {
                        workflowModel!!.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
                    }
                }

            }
        }
    }

    fun extractIdFromUrl(url: String): Long? {
        ///api/product/ 다음의 숫자를 추출
        val pattern = "https://hlivv.com/product/(\\d+)".toRegex()
        val matchResult = pattern.find(url)
        return matchResult?.groupValues?.getOrNull(1)?.toLong()
    }

    fun getProductImageUrl(productImages: Array<ProductImageDto>?): String {
        return productImages?.firstOrNull()?.imageUrl ?: ""
    }

    fun convertProductDtoToProduct(productId: Long, productDto: ProductDto): Product {
        val imageUrl = getProductImageUrl(productDto.productImages)
        val title = productDto.name ?: ""
        val price = productDto.price?.toString() ?: "0"

        return Product(
            productId, imageUrl, title, price,
            productDto.arSupported!!, productDto.qrSupported!!, productDto.eco!!
        )
    }


}
