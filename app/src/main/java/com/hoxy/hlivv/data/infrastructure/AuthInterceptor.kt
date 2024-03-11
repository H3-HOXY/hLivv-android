package com.hoxy.hlivv.data.infrastructure

import android.content.Context
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.apis.AuthControllerApi
import com.hoxy.hlivv.data.repository.PreferencesRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

//class AuthInterceptor(private val contextProvider: () -> Context) : Interceptor {
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//
//        // 토큰 가져오기
//        val preferencesRepository=PreferencesRepository(contextProvider())
//        val token=preferencesRepository.getStringPref(R.string.pref_key_token,"")
//
//        // 토큰이 있으면 헤더에 추가
//        val newRequest = if (token != null && token.isNotEmpty()) {
//            originalRequest.newBuilder()
//                .header("Authorization", "Bearer $token")
//                .build()
//        } else {
//            originalRequest
//        }
//
//        return chain.proceed(newRequest)
//    }
//}

class AuthInterceptor(private val contextProvider: () -> Context) : Interceptor {
    val authControllerApi = AuthControllerApi()

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // 토큰 가져오기
        val preferencesRepository = PreferencesRepository(contextProvider())
        var token = preferencesRepository.getStringPref(R.string.pref_key_token, "")

        // 토큰이 있으면 헤더에 추가
        var newRequest = if (token.isNotEmpty()) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        // 요청 실행
        var response = chain.proceed(newRequest)

        // 토큰 만료 오류 처리
        if (response.code == 401) {
            // 로그인 정보 가져오기
            val loginInfo = preferencesRepository.getLoginInfo()
            if (loginInfo.loginId.isNotEmpty() && loginInfo.loginPw.isNotEmpty()) {
                // 토큰 재발급
                runBlocking {
                    val tokenDto = authControllerApi.authorize(loginInfo)
                    token = tokenDto.token!!
                    preferencesRepository.saveStringPref(R.string.pref_key_token, token)
                }

                // 재발급 받은 토큰으로 다시 요청
                newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()

                response = chain.proceed(newRequest)

            }

        }

        return response
    }
}
