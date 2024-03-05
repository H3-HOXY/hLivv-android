package com.hoxy.hlivv.ui.liveqr.barcode


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.hoxy.hlivv.domain.PreferenceUtils
import com.hoxy.hlivv.ui.liveqr.barcode.animation.CameraReticleAnimator
import com.hoxy.hlivv.ui.liveqr.barcode.graphic.BarcodeConfirmingGraphic
import com.hoxy.hlivv.ui.liveqr.barcode.graphic.BarcodeLoadingGraphic
import com.hoxy.hlivv.ui.liveqr.barcode.graphic.BarcodeReticleGraphic
import com.hoxy.hlivv.ui.liveqr.barcode.graphic.GraphicOverlay
import com.hoxy.hlivv.ui.liveqr.camera.WorkflowModel


class BarcodeAnalyzer(
    private val workflowModel: WorkflowModel,
    private val graphicOverlay: GraphicOverlay
) :
    ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    processBarcodeResults(barcodes)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun processBarcodeResults(barcodes: List<Barcode>) {
        val barcodeInCenter = barcodes.firstOrNull { barcode ->
            val boundingBox = barcode.boundingBox ?: return@firstOrNull false
            Log.d("Barcode", "$barcode")
            val box = graphicOverlay.translateRect(boundingBox)
            // 애뮬레이터
            //box.contains(graphicOverlay.width / 2f, graphicOverlay.height / 2f)
            // 실물 폰
            box.contains(graphicOverlay.width / 4f, graphicOverlay.height / 7f)
            //box.contains(graphicOverlay.width / 4f, graphicOverlay.height /5f)
        }

        graphicOverlay.clear()
        if (barcodeInCenter == null) {
            cameraReticleAnimator.start()
            graphicOverlay.add(BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator))
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        } else {
            cameraReticleAnimator.cancel()
            val sizeProgress = PreferenceUtils.getProgressToMeetBarcodeSizeRequirement(
                graphicOverlay,
                barcodeInCenter
            )
            if (sizeProgress < 1) {
                graphicOverlay.add(BarcodeConfirmingGraphic(graphicOverlay, barcodeInCenter))
                workflowModel.setWorkflowState(WorkflowModel.WorkflowState.CONFIRMING)
            } else {
                if (PreferenceUtils.shouldDelayLoadingBarcodeResult(graphicOverlay.context)) {
                    val loadingAnimator = createLoadingAnimator(graphicOverlay, barcodeInCenter)
                    loadingAnimator.start()
                    graphicOverlay.add(BarcodeLoadingGraphic(graphicOverlay, loadingAnimator))
                    workflowModel.setWorkflowState(WorkflowModel.WorkflowState.SEARCHING)
                } else {
                    workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTED)
                    workflowModel.detectedBarcode.postValue(barcodeInCenter)
                }
            }
        }
        graphicOverlay.invalidate()
    }

    private fun createLoadingAnimator(
        graphicOverlay: GraphicOverlay,
        barcode: Barcode
    ): ValueAnimator {
        val endProgress = 1.1f
        return ValueAnimator.ofFloat(0f, endProgress).apply {
            duration = 2000
            addUpdateListener {
                if ((animatedValue as Float).compareTo(endProgress) >= 0) {
                    graphicOverlay.clear()
                    workflowModel.setWorkflowState(WorkflowModel.WorkflowState.SEARCHED)
                    workflowModel.detectedBarcode.postValue(barcode)
                } else {
                    graphicOverlay.invalidate()
                }
            }
        }
    }

}
