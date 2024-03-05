package com.hoxy.hlivv.domain

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.WindowCompat
import java.text.NumberFormat
import java.util.Locale

object Utils {
    fun Activity.setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if (Build.VERSION.SDK_INT >= 30) {    // API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    fun setFormattedNumberToTextView(number: Long, textView: TextView) {
        val formattedNumber = NumberFormat.getNumberInstance(Locale.KOREA).format(number)
        textView.text = formattedNumber
    }

    fun setFormattedIntToTextView(number: Int, textView: TextView) {
        val formattedNumber = NumberFormat.getNumberInstance(Locale.KOREA).format(number)
        textView.text = formattedNumber
    }
//
//    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
//        if (exception is ClientException) {
//            val response = JSONObject(exception.message)
//            val message = response.getString("message")
//
//        }
//    }

    fun showErrorDialog(msg: String, context: Context) {
        Handler(Looper.getMainLooper()).post {
            val errorHandlerDialog = ErrorHandlerDialog(context, msg)
//            errorHandlerDialog.listener = object : OnDialogConfirmedListener {
//                override fun onDialogConfirmed() {
//                    listener.onDialogConfirmed()
//                }
//            }
            errorHandlerDialog.start()
        }
    }

}