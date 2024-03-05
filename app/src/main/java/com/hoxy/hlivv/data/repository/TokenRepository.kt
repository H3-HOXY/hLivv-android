package com.hoxy.hlivv.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TokenRepository(private val context: Context) {

    private val _tokenLiveData = MutableLiveData<String?>()
    val tokenLiveData: LiveData<String?>
        get() = _tokenLiveData

    init {
        // SharedPreferences에 토큰 값이 바뀌었는지 감지하는 리스너 추가
        val prefs = TokenManager.getEncryptedSharedPreferences(context)

        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "pref_key_token") {
                _tokenLiveData.postValue(TokenManager.getTokenFromSharedPreferences(context))
            }
        }

        // 초기 토큰 값을 셋업
        _tokenLiveData.postValue(TokenManager.getTokenFromSharedPreferences(context))
    }
}
