package com.hoxy.hlivv.domain

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.infrastructure.ClientException
import com.hoxy.hlivv.data.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
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

    fun showErrorDialog(msg: String, context: Context) {
        Handler(Looper.getMainLooper()).post {
            val errorHandlerDialog = ErrorHandlerDialog(context, msg)
            errorHandlerDialog.start()
        }
    }

    suspend fun handleApiError(
        e: Exception,
        navController: NavController,
        context: Context,
        customErrorMessage: String? = null
    ) {
        try {
            if (e is ClientException) {
                val response = JSONObject(e.message)
                val status = response.getInt("status")
                val message = response.getString("message")

                if (status == 401) {
                    navController.navigate(R.id.navigation_login)
                } else {
                    showErrorDialog(message, context)
                }
            } else {
                Log.d("Coroutine","Error",e)
                showErrorDialog(customErrorMessage ?: "잠시 후 다시 시도해주세요.", context)
            }
        } catch (e:JSONException){
            if(e.stackTrace.any { it.toString().contains("org.json.JSONTokener.syntaxError") }){
                withContext(Dispatchers.Main){
                    navController.navigate(R.id.navigation_login)
                }

            } else{
                showErrorDialog(customErrorMessage ?: "잠시 후 다시 시도해주세요.", context)
            }
        }
        catch (e: Exception) {
            Log.d("Coroutine","Error",e)
            showErrorDialog(customErrorMessage ?: "잠시 후 다시 시도해주세요.", context)
        }
    }

    fun handleApiError(
        e: Exception,
        context: Context,
        customErrorMessage: String? = null
    ) {
        try {
            if (e is ClientException) {
                val response = JSONObject(e.message)
                val status = response.getInt("status")
                val message = response.getString("message")

                if (status == 401 || status == 400) {
                    showErrorDialog("로그인 후 이용해주세요", context)
                } else {
                    showErrorDialog(message, context)
                }
            } else {
                showErrorDialog(customErrorMessage ?: "잠시 후 다시 시도해주세요.", context)
            }
        } catch (e: Exception) {
            showErrorDialog(customErrorMessage ?: "잠시 후 다시 시도해주세요.", context)
        }
    }


}