package com.hoxy.hlivv.ui.liveqr.barcode.product

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.Log
import android.widget.ImageView
import com.hoxy.hlivv.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL


class ImageDownloadTask(private val imageView: ImageView, private val maxImageWidth: Int) {
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun downloadImage(url: String) {
        coroutineScope.launch {
            val bitmap = downloadBitmap(url)
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun loadDefaultImage(): Bitmap {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 4
        }
        return BitmapFactory.decodeResource(
            imageView.context.resources,
            R.drawable.no_image,
            options
        )
    }


    private fun downloadBitmap(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream = URL(url).openStream()
            //         bitmap = BitmapFactory.decodeStream(inputStream)
            val options = BitmapFactory.Options().apply {
                inSampleSize = 4
            }

            bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            //bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Image download failed: $url", e)
            return loadDefaultImage()

        }

        bitmap = bitmap?.let {
            if (it.width > maxImageWidth) {
                val dstHeight = (maxImageWidth.toFloat() / it.width * it.height).toInt()
                Bitmap.createScaledBitmap(it, maxImageWidth, dstHeight, /* filter= */ false)
            } else {
                it
            }
        }

        bitmap = bitmap?.let {
            val output = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val paint = Paint()
            paint.isAntiAlias = true
            paint.strokeWidth = 0f

            val rect = Rect(0, 0, it.width, it.height)
            val rectF = RectF(rect)

            // 둥근 모서리의 반지름 설정
            val radius = 16f

            canvas.drawRoundRect(rectF, radius, radius, paint)

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            val shader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = shader

            canvas.drawRoundRect(rectF, radius, radius, paint)

            output
        }


        return bitmap

    }

    fun cancel() {
        job.cancel()
    }

    companion object {
        private const val TAG = "ImageDownloadTask"
    }
}
