package com.hoxy.hlivv.data.repository

import android.content.Context
import androidx.annotation.StringRes
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.hoxy.hlivv.data.models.LoginDto


/**
 * @author 반정현
 */
class PreferencesRepository(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "myHLivvAppPreferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveStringPref(@StringRes prefKeyId: Int, value: String) {
        val prefKey = context.getString(prefKeyId)
        with(sharedPreferences.edit()) {
            putString(prefKey, value)
            apply()
        }
    }

    fun getStringPref(@StringRes prefKeyId: Int, defaultValue: String): String {
        val prefKey = context.getString(prefKeyId)
        return sharedPreferences.getString(prefKey, defaultValue) ?: defaultValue
    }

    fun saveLoginInfo(loginInfo: LoginDto) {
        sharedPreferences.edit().apply {
            putString("loginId", loginInfo.loginId)
            putString("loginPw", loginInfo.loginPw)
            apply()
        }
    }

    fun getLoginInfo(): LoginDto {
        val loginId = sharedPreferences.getString("loginId", "")
        val loginPw = sharedPreferences.getString("loginPw", "")
        return LoginDto(loginId.toString(), loginPw.toString())
    }

}

