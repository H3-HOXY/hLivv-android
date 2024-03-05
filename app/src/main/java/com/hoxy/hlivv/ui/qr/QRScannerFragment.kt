//package com.hoxy.hlivv.ui.qr
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AlertDialog
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.google.common.util.concurrent.ListenableFuture
//import com.hoxy.hlivv.databinding.FragmentQrScannerBinding
//import java.util.concurrent.Executors
//
//class QRScannerFragment : Fragment() {
//    private var _binding: FragmentQrScannerBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
//    private var isDetected = false
//    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//            if (isGranted) {
//                Toast.makeText(requireContext(), "권한 요청이 승인되었습니다.", Toast.LENGTH_LONG).show()
//                startCamera()
//            } else requireActivity().supportFragmentManager.popBackStack()
//        }
//
//    override fun onResume() {
//        super.onResume()
//        isDetected=false
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentQrScannerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        if (!hasPermissions(requireContext())) {
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//        } else {
//            startCamera()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
//        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun startCamera() {
//        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            val preview = getPreview()
//            val imageAnalysis = getImageAnalysis()
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }
//
//    private fun getPreview(): Preview {
//        val preview: Preview = Preview.Builder().build()
//        preview.setSurfaceProvider(binding.barcodePreview.getSurfaceProvider())
//        return preview
//    }
//
//    private fun getImageAnalysis(): ImageAnalysis {
//        val imageAnalysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
//        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), QRCodeAnalyzer(object : OnDetectListener {
//            override fun onDetect(msg: String) {
//                if (!isDetected) {
//                    isDetected = true
//                    showResultDialog(msg)
//                }
//            }
//        }))
//        return imageAnalysis
//    }
//
//    private fun showResultDialog(msg: String) {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("QR Code Detected")
//        builder.setMessage(msg)
//        builder.setPositiveButton("OK") { dialog, _ ->
//            isDetected=false
//            dialog.dismiss()
//        }
//        val dialog = builder.create()
//        dialog.show()
//    }
//}
