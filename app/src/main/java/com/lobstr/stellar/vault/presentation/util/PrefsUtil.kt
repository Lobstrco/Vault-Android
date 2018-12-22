package com.lobstr.stellar.vault.presentation.util

import android.content.SharedPreferences
import android.util.Base64


class PrefsUtil(private val sharedPreferences: SharedPreferences) {

    internal companion object {
        const val PREF_AUTH_TOKEN = "PREF_AUTH_TOKEN"
        const val PREF_SIGNED_ACCOUNT = "PREF_SIGNED_ACCOUNT"
        const val PREF_PUBLIC_KEY = "PREF_PUBLIC_KEY"
        const val PREF_ENCRYPTED_PHRASES = "PREF_ENCRYPTED_PHRASES"
        const val PREF_ENCRYPTED_PIN = "PREF_ENCRYPTED_PIN"
        const val PREF_PHRASES_IV = "PREF_PHRASES_IV"
        const val PREF_PIN_IV = "PREF_PIN_IV"
        const val PREF_FCM_TOKEN = "PREF_FCM_TOKEN"
        const val PREF_APP_VERSION = "PREF_APP_VERSION"
        const val PREF_IS_FCM_REGISTERED_SUCCESSFULLY = "PREF_IS_FCM_REGISTERED_SUCCESSFULLY"
        const val PREF_IS_USER_SIGNER_FOR_LOBSTR = "PREF_IS_USER_SIGNER_FOR_LOBSTR"
    }

    var authToken: String?
        get() = getString(PREF_AUTH_TOKEN)
        set(authToken) = set(PREF_AUTH_TOKEN, authToken)

    var signedAccount: String?
        get() = getString(PREF_SIGNED_ACCOUNT)
        set(accountId) = set(PREF_SIGNED_ACCOUNT, accountId)

    var publicKey: String?
        get() = getString(PREF_PUBLIC_KEY)
        set(publicKey) = set(PREF_PUBLIC_KEY, publicKey)

    var encryptedPhrases: String?
        get() = getString(PREF_ENCRYPTED_PHRASES)
        set(encryptedData) = set(PREF_ENCRYPTED_PHRASES, encryptedData)

    var encryptedPin: String?
        get() = getString(PREF_ENCRYPTED_PIN)
        set(encryptedData) = set(PREF_ENCRYPTED_PIN, encryptedData)

    var phrasesIv: String?
        get() = getString(PREF_PHRASES_IV)
        set(value) = set(PREF_PHRASES_IV, value)

    var pinIv: String?
        get() = getString(PREF_PIN_IV)
        set(value) = set(PREF_PIN_IV, value)

    var fcmToken: String?
        get() = getString(PREF_FCM_TOKEN)
        set(fcmToken) = set(PREF_FCM_TOKEN, fcmToken)

    var appVersion: Int
        get() = getInt(PREF_APP_VERSION)
        set(appVersion) = set(PREF_APP_VERSION, appVersion)

    var isFcmRegisteredSuccessfully: Boolean
        get() = getBoolean(PREF_IS_FCM_REGISTERED_SUCCESSFULLY)
        set(isRegistered) = set(PREF_IS_FCM_REGISTERED_SUCCESSFULLY, isRegistered)

    var isUserSignerForLobstr: Boolean
        get() = getBoolean(PREF_IS_USER_SIGNER_FOR_LOBSTR)
        set(isUserSigner) = set(PREF_IS_USER_SIGNER_FOR_LOBSTR, isUserSigner)

    operator fun set(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    operator fun set(key: String, value: ByteArray) {
        val base64 = Base64.encodeToString(value, Base64.DEFAULT)
        sharedPreferences.edit().putString(key, base64).apply()
    }

    operator fun set(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    operator fun set(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, Int.MIN_VALUE)
    }

    fun clearUserPrefs(): Boolean {
        val editor = sharedPreferences.edit()
        editor.remove(PREF_AUTH_TOKEN)
        editor.remove(PREF_SIGNED_ACCOUNT)
        editor.remove(PREF_PUBLIC_KEY)
        editor.remove(PREF_ENCRYPTED_PHRASES)
        editor.remove(PREF_FCM_TOKEN)
        editor.remove(PREF_APP_VERSION)
        editor.remove(PREF_IS_FCM_REGISTERED_SUCCESSFULLY)
        editor.remove(PREF_IS_USER_SIGNER_FOR_LOBSTR)
        return editor.commit()
    }
}