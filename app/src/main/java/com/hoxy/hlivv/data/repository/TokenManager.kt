package com.hoxy.hlivv.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.AlgorithmParameterSpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object TokenManager {

    private const val KEY_ALIAS = "myKeyAlias"
    private const val SHARED_PREFS_NAME = "my_encrypted_prefs"
    private var initialized = false

    // 키 페어 생성
    private fun generateKeyPair(context: Context) {
        if (!initialized) {
            try {
                val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
                val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY or
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).run {
                    setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    build()
                }
                keyPairGenerator.initialize(parameterSpec)
                keyPairGenerator.generateKeyPair()
                initialized = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 개인키 얻기
    private fun getPrivateKey(): PrivateKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).privateKey
    }

    // 공개키 얻기
    private fun getPublicKey(context: Context): PublicKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getCertificate(KEY_ALIAS).publicKey
    }

    // 데이터에 서명 생성
    private fun signData(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    // AES 키 얻기 (최초 한 번만 생성)
    private val secretKey: SecretKey by lazy {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setRandomizedEncryptionRequired(false)
            build()
        }
        keyGenerator.init(parameterSpec)
        keyGenerator.generateKey()
    }

    // 비밀키로 데이터 암호화
    private fun encryptWithSecretKey(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12) // IV는 12바이트로 설정 (GCM 모드의 요구 사항)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        return cipher.doFinal(data)
    }

    private fun decryptWithSecretKey(encryptedData: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12) // IV는 12바이트로 설정 (GCM 모드의 요구 사항)
        val spec: AlgorithmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedData)
    }


    // 서명된 토큰 암호화 및 반환
    fun encryptAndSignToken(context: Context, token: String): String {
        generateKeyPair(context) // 키 페어 생성
        val privateKey = getPrivateKey() // 개인키 얻기

        // 토큰에 서명 생성
        val signature = signData(token.toByteArray(), privateKey)

        // 토큰과 서명을 결합 및 AES로 암호화
        val tokenWithSignature = "$token.${Base64.getEncoder().encodeToString(signature)}"
        val encryptedToken = encryptWithSecretKey(tokenWithSignature.toByteArray())

        // 암호화된 토큰을 Base64로 인코딩하여 반환
        return Base64.getEncoder().encodeToString(encryptedToken)
    }

    // 토큰 저장
    fun saveToken(context: Context, token: String) {
        val encryptedToken = encryptAndSignToken(context, token)
        saveTokenToSharedPreferences(context, encryptedToken)
    }

    // SharedPreferences에 토큰 저장
    private fun saveTokenToSharedPreferences(context: Context, token: String) {
        getEncryptedSharedPreferences(context).edit().putString("pref_key_token", token).apply()
    }

    // SharedPreferences에서 토큰 얻기 및 복호화
    fun getTokenFromSharedPreferences(context: Context): String? {
        val encryptedToken =
            getEncryptedSharedPreferences(context).getString("pref_key_token", null)
        return if (encryptedToken != null) {
            decryptAndVerifyToken(context, encryptedToken)
        } else {
            null
        }
    }

    fun removeToken(context: Context) {
        getEncryptedSharedPreferences(context).edit().remove("pref_key_token").apply()
    }

    // 암호화된 토큰을 복호화하고 서명 검증
    private fun decryptAndVerifyToken(context: Context, encryptedToken: String): String? {
        val encryptedTokenBytes = Base64.getDecoder().decode(encryptedToken)
        val decryptedTokenWithSignature: ByteArray = decryptWithSecretKey(encryptedTokenBytes)
        val tokenString = String(decryptedTokenWithSignature, Charsets.UTF_8)

        val parts = tokenString.split("\\.".toRegex())
        if (parts.size == 2) {
            val token = parts[0]
            val signature = Base64.getDecoder().decode(parts[1])

            if (verifySignature(token.toByteArray(), signature, getPublicKey(context))) {
                return token
            }
        }

        return null
    }

    // 서명 검증
    private fun verifySignature(
        data: ByteArray,
        signature: ByteArray,
        publicKey: PublicKey
    ): Boolean {
        val verifySignature = Signature.getInstance("SHA256withRSA")
        verifySignature.initVerify(publicKey)
        verifySignature.update(data)
        return verifySignature.verify(signature)
    }

    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKey = getMasterKey(context)
        return EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


}
