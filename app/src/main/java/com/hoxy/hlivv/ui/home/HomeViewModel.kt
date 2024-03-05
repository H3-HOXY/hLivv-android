package com.hoxy.hlivv.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _loginStatus = MutableLiveData<String>()
    val loginStatus: LiveData<String> get() = _loginStatus

    fun checkLogin(jwtToken: String?) {
        _loginStatus.value = if (jwtToken != null && jwtToken.isNotEmpty()) "로그아웃" else "로그인"
    }
}


