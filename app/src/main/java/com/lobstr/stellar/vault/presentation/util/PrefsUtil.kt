package com.lobstr.stellar.vault.presentation.util

import android.content.SharedPreferences


class PrefsUtil(private val sharedPreferences: SharedPreferences) {

    private companion object {
        const val PREF_AUTH_TOKEN = "PREF_AUTH_TOKEN"
        const val PREF_USER_ID = "PREF_USER_ID"
        const val PREF_PUBLIC_KEY = "PREF_PUBLIC_KEY"
        const val PREF_ENCRYPTED_KEY = "PREF_ENCRYPTED_KEY"
        const val PREF_FCM_TOKEN = "PREF_FCM_TOKEN"
        const val PREF_APP_VERSION = "PREF_APP_VERSION"
        const val PREF_IS_FCM_REGISTERED_SUCCESSFULLY = "PREF_IS_FCM_REGISTERED_SUCCESSFULLY"
    }

    var authToken: String?
        get() = getString(PREF_AUTH_TOKEN)
        set(authToken) = set(PREF_AUTH_TOKEN, authToken)

    var stellarAccountId: String?
        get() = getString(PREF_USER_ID)
        set(accountId) = set(PREF_USER_ID, accountId)

    var publicKey: String?
        get() = getString(PREF_PUBLIC_KEY)
        set(publicKey) = set(PREF_PUBLIC_KEY, publicKey)

    var encryptedKey: String?
        get() = getString(PREF_ENCRYPTED_KEY)
        set(encryptedData) = set(PREF_ENCRYPTED_KEY, encryptedData)

    var fcmToken: String?
        get() = getString(PREF_FCM_TOKEN)
        set(fcmToken) = set(PREF_FCM_TOKEN, fcmToken)

    var appVersion: Int
        get() = getInt(PREF_APP_VERSION)
        set(appVersion) = set(PREF_APP_VERSION, appVersion)

    var isFcmRegisteredSuccessfully: Boolean
        get() = getBoolean(PREF_IS_FCM_REGISTERED_SUCCESSFULLY)
        set(isRegistered) = set(PREF_IS_FCM_REGISTERED_SUCCESSFULLY, isRegistered)

    private operator fun set(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private operator fun set(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private operator fun set(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    private fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    private fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    private fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, Int.MIN_VALUE)
    }

    fun clearUserPrefs(): Boolean {
        val editor = sharedPreferences.edit()
        editor.remove(PREF_AUTH_TOKEN)
        editor.remove(PREF_USER_ID)
        editor.remove(PREF_PUBLIC_KEY)
        editor.remove(PREF_ENCRYPTED_KEY)
        editor.remove(PREF_FCM_TOKEN)
        editor.remove(PREF_APP_VERSION)
        editor.remove(PREF_IS_FCM_REGISTERED_SUCCESSFULLY)
        return editor.commit()
    }
}