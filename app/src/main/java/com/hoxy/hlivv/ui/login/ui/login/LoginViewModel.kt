package com.hoxy.hlivv.ui.login.ui.login

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.apis.AuthControllerApi
import com.hoxy.hlivv.data.infrastructure.ClientException
import com.hoxy.hlivv.data.infrastructure.ServerException
import com.hoxy.hlivv.data.models.LoginDto
import com.hoxy.hlivv.data.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(private val authControllerApi: AuthControllerApi, application: Application) :
    AndroidViewModel(application) {

    private val _loginForm = MutableLiveData<LoginFormState>()
    private val appContext = application.applicationContext
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tokenDto = authControllerApi.authorize(LoginDto(username, password))
                Log.d("Login", tokenDto.toString())
                //TokenManager.saveToken(appContext,tokenDto.toString())
                val preferencesRepository = PreferencesRepository(appContext)
                preferencesRepository.saveStringPref(
                    R.string.pref_key_token,
                    tokenDto.token.toString()
                )
                preferencesRepository.saveLoginInfo(LoginDto(username, password))

                //Log.d("Login", TokenManager.getTokenFromSharedPreferences(appContext).toString())
                _loginResult.postValue(LoginResult(success = R.string.action_sign_in_short))
            } catch (e: ClientException) {
                _loginResult.postValue(LoginResult(error = R.string.client_error))
            } catch (e: ServerException) {
                _loginResult.postValue(LoginResult(error = R.string.server_error))
            } catch (e: Exception) {
                Log.d("Login", "Exception", e)
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }

        // can be launched in a separate asynchronous job

//        val result = loginRepository.login(username, password)
//
//        if (result is Result.Success) {
//            _loginResult.value =
//                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
//        } else {
//            _loginResult.value = LoginResult(error = R.string.login_failed)
//        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

//    private fun saveStringPref(context: Context, @StringRes prefKeyId: Int, value: String) {
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val prefKey = context.getString(prefKeyId)
//        with(sharedPreferences.edit()) {
//            putString(prefKey, value)
//            apply()
//        }
//    }
//
//    private fun getJwtToken(context: Context, @StringRes prefKeyId: Int, defaultValue: String): String {
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val prefKey = context.getString(prefKeyId)
//        return sharedPreferences.getString(prefKey, defaultValue) ?: defaultValue
//    }

}