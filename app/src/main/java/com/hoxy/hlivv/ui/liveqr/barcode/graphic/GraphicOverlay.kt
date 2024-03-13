package com.hoxy.hlivv.ui.liveqr.barcode.graphic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.camera.view.PreviewView
import com.hoxy.hlivv.domain.CameraUtils

/**
 * @author 반정현
 */
class GraphicOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val lock = Any()

    private var previewWidth: Int = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight: Int = 0
    private var heightScaleFactor = 1.0f
    private val graphics = ArrayList<Graphic>()

    abstract class Graphic protected constructor(protected val overlay: GraphicOverlay) {
        protected val context: Context = overlay.context

        abstract fun draw(canvas: Canvas)
    }

    fun clear() {
        synchronized(lock) {
            graphics.clear()
        }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) {
            graphics.add(graphic)
        }
    }

    fun setCameraInfo(previewView: PreviewView) {
        if (CameraUtils.isPortraitMode(context)) {
            previewWidth = previewView.width
            previewHeight = previewView.height
        } else {
            previewWidth = previewView.height
            previewHeight = previewView.width
        }

    }

    fun translateX(x: Float): Float = x * widthScaleFactor
    fun translateY(y: Float): Float = y * heightScaleFactor

    fun translateRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        translateY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        translateY(rect.bottom.toFloat())
    )

    override fun onDraw(canvas: Canvas) {
        if (previewWidth > 0 && previewHeight > 0) {
            widthScaleFactor = width.toFloat() / previewWidth
            heightScaleFactor = height.toFloat() / previewHeight

        }

        super.onDraw(canvas)

        synchronized(lock) {
            graphics.forEach { it.draw(canvas) }
        }
    }

}