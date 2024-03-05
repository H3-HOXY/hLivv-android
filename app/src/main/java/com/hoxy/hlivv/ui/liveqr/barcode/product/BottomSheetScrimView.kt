package com.hoxy.hlivv.ui.liveqr.barcode.product

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.common.internal.Preconditions.checkArgument
import com.hoxy.hlivv.R

class BottomSheetScrimView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val scrimPaint: Paint
    private val thumbnailPaint: Paint
    private val boxPaint: Paint
    private val thumbnailHeight: Int
    private val thumbnailMargin: Int
    private val boxCornerRadius: Int

    private var thumbnailBitmap: Bitmap? = null
    private var thumbnailRect: RectF? = null
    private var downPercentInCollapsed: Float = 0f

    init {
        val resources = context.resources
        scrimPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.dark)
        }

        thumbnailPaint = Paint()

        boxPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth =
                resources.getDimensionPixelOffset(R.dimen.object_thumbnail_stroke_width).toFloat()
            color = Color.WHITE
        }

        thumbnailHeight = resources.getDimensionPixelOffset(R.dimen.object_thumbnail_height)
        thumbnailMargin = resources.getDimensionPixelOffset(R.dimen.object_thumbnail_margin)
        boxCornerRadius = resources.getDimensionPixelOffset(R.dimen.bounding_box_corner_radius)
    }


    fun updateWithThumbnailTranslate(
        thumbnailBitmap: Bitmap,
        collapsedStateHeight: Int,
        slideOffset: Float,
        bottomSheet: View
    ) {
        this.thumbnailBitmap = thumbnailBitmap

        val currentSheetHeight: Float
        if (slideOffset < 0) {
            downPercentInCollapsed = -slideOffset
            currentSheetHeight = collapsedStateHeight * (1 + slideOffset)
        } else {
            downPercentInCollapsed = 0f
            currentSheetHeight =
                collapsedStateHeight + (bottomSheet.height - collapsedStateHeight) * slideOffset
        }

        thumbnailRect = RectF().apply {
            val thumbnailWidth =
                thumbnailBitmap.width.toFloat() / thumbnailBitmap.height.toFloat() * thumbnailHeight.toFloat()
            left = thumbnailMargin.toFloat()
            top =
                height.toFloat() - currentSheetHeight - thumbnailMargin.toFloat() - thumbnailHeight.toFloat()
            right = left + thumbnailWidth
            bottom = top + thumbnailHeight
        }

        invalidate()
    }


    fun updateWithThumbnailTranslateAndScale(
        thumbnailBitmap: Bitmap,
        collapsedStateHeight: Int,
        slideOffset: Float,
        srcThumbnailRect: RectF
    ) {
        checkArgument(
            slideOffset <= 0,
            "Scale mode works only when the sheet is between hidden and collapsed states."
        )

        this.thumbnailBitmap = thumbnailBitmap
        this.downPercentInCollapsed = 0f

        thumbnailRect = RectF().apply {
            val dstX = thumbnailMargin.toFloat()
            val dstY = (height - collapsedStateHeight - thumbnailMargin - thumbnailHeight).toFloat()
            val dstHeight = thumbnailHeight.toFloat()
            val dstWidth = srcThumbnailRect.width() / srcThumbnailRect.height() * dstHeight
            val dstRect = RectF(dstX, dstY, dstX + dstWidth, dstY + dstHeight)

            val progressToCollapsedState = 1 + slideOffset
            left =
                srcThumbnailRect.left + (dstRect.left - srcThumbnailRect.left) * progressToCollapsedState
            top =
                srcThumbnailRect.top + (dstRect.top - srcThumbnailRect.top) * progressToCollapsedState
            right =
                srcThumbnailRect.right + (dstRect.right - srcThumbnailRect.right) * progressToCollapsedState
            bottom =
                srcThumbnailRect.bottom + (dstRect.bottom - srcThumbnailRect.bottom) * progressToCollapsedState
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draws the dark background.
        val bitmap = thumbnailBitmap ?: return
        val rect = thumbnailRect ?: return
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
        if (downPercentInCollapsed < DOWN_PERCENT_TO_HIDE_THUMBNAIL) {
            val alpha =
                ((1 - downPercentInCollapsed / DOWN_PERCENT_TO_HIDE_THUMBNAIL) * 255).toInt()

            // Draws the object thumbnail.
            thumbnailPaint.alpha = alpha
            canvas.drawBitmap(bitmap, null, rect, thumbnailPaint)

            // Draws the bounding box.
            boxPaint.alpha = alpha
            canvas.drawRoundRect(
                rect,
                boxCornerRadius.toFloat(),
                boxCornerRadius.toFloat(),
                boxPaint
            )
        }
    }

    companion object {
        private const val DOWN_PERCENT_TO_HIDE_THUMBNAIL = 0.42f
    }
}