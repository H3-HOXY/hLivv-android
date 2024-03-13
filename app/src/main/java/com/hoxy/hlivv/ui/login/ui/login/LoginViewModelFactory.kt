package com.hoxy.hlivv.ui.login.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hoxy.hlivv.data.apis.AuthControllerApi

/**
 * @author 반정현
 */
/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(
    private val authControllerApi: AuthControllerApi,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authControllerApi, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
