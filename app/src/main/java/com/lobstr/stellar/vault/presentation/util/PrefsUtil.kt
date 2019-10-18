package com.lobstr.stellar.vault.presentation.util

import android.content.SharedPreferences
import android.util.Base64


class PrefsUtil(private val sharedPreferences: SharedPreferences) {

    internal companion object {
        const val PREF_AUTH_TOKEN = "PREF_AUTH_TOKEN"
        const val PREF_PUBLIC_KEY = "PREF_PUBLIC_KEY"
        const val PREF_ENCRYPTED_PHRASES = "PREF_ENCRYPTED_PHRASES"
        const val PREF_ENCRYPTED_PIN = "PREF_ENCRYPTED_PIN"
        const val PREF_PHRASES_IV = "PREF_PHRASES_IV"
        const val PREF_PIN_IV = "PREF_PIN_IV"
        const val PREF_FCM_TOKEN = "PREF_FCM_TOKEN"
        const val PREF_APP_VERSION = "PREF_APP_VERSION"
        const val PREF_IS_FCM_REGISTERED_SUCCESSFULLY = "PREF_IS_FCM_REGISTERED_SUCCESSFULLY"
        const val PREF_ACCOUNT_HAS_SIGNERS = "PREF_ACCOUNT_HAS_SIGNERS"
        const val PREF_BIOMETRIC_STATE = "PREF_BIOMETRIC_STATE"
        const val PREF_RATE_US_STATE = "PREF_RATE_US_STATE"
        const val PREF_IS_NOTIFICATIONS_ENABLED = "PREF_IS_NOTIFICATIONS_ENABLED"
        const val PREF_IS_TR_CONFIRMATION_ENABLED = "PREF_IS_TR_CONFIRMATION_ENABLED"
        const val PREF_ACCOUNT_SIGNERS_COUNT = "PREF_ACCOUNT_SIGNERS_COUNT"
    }

    var authToken: String?
        get() = getString(PREF_AUTH_TOKEN)
        set(authToken) = set(PREF_AUTH_TOKEN, authToken)

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

    var appVersion: Long
        get() = getLong(PREF_APP_VERSION)
        set(appVersion) = set(PREF_APP_VERSION, appVersion)

    var isFcmRegisteredSuccessfully: Boolean
        get() = getBoolean(PREF_IS_FCM_REGISTERED_SUCCESSFULLY)
        set(isRegistered) = set(PREF_IS_FCM_REGISTERED_SUCCESSFULLY, isRegistered)

    var accountHasSigners: Boolean
        get() = getBoolean(PREF_ACCOUNT_HAS_SIGNERS)
        set(hasSigners) = set(PREF_ACCOUNT_HAS_SIGNERS, hasSigners)

    /**
     * @see Constant.BiometricState
     */
    var biometricState: Int
        get() = getInt(PREF_BIOMETRIC_STATE)
        set(state) = set(PREF_BIOMETRIC_STATE, state)

    /**
     * @see Constant.RateUsState
     */
    var rateUsState: Int
        get() = getInt(PREF_RATE_US_STATE)
        set(state) = set(PREF_RATE_US_STATE, state)

    var isNotificationsEnabled: Boolean
        get() = getBoolean(PREF_IS_NOTIFICATIONS_ENABLED)
        set(enabled) = set(PREF_IS_NOTIFICATIONS_ENABLED, enabled)

    var isTrConfirmationEnabled: Boolean
        get() = getBoolean(PREF_IS_TR_CONFIRMATION_ENABLED)
        set(enabled) = set(PREF_IS_TR_CONFIRMATION_ENABLED, enabled)

    var accountSignersCount: Int
        get() = getInt(PREF_ACCOUNT_SIGNERS_COUNT)
        set(count) = set(PREF_ACCOUNT_SIGNERS_COUNT, count)

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

    operator fun set(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBoolean(key: String): Boolean {
        var devValue = false

        // set default value for specific cases
        when (key) {
            PREF_IS_NOTIFICATIONS_ENABLED -> devValue = true
            PREF_IS_TR_CONFIRMATION_ENABLED -> devValue = true
        }

        return sharedPreferences.getBoolean(key, devValue)
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, 0)
    }

    fun clearUserPrefs(): Boolean {
        val editor = sharedPreferences.edit()
        editor.remove(PREF_AUTH_TOKEN)
        editor.remove(PREF_PUBLIC_KEY)
        editor.remove(PREF_ENCRYPTED_PHRASES)
        editor.remove(PREF_ENCRYPTED_PIN)
        editor.remove(PREF_PHRASES_IV)
        editor.remove(PREF_PIN_IV)
        editor.remove(PREF_FCM_TOKEN)
        editor.remove(PREF_APP_VERSION)
        editor.remove(PREF_IS_FCM_REGISTERED_SUCCESSFULLY)
        editor.remove(PREF_ACCOUNT_HAS_SIGNERS)
        editor.remove(PREF_BIOMETRIC_STATE)
        editor.remove(PREF_IS_NOTIFICATIONS_ENABLED)
        editor.remove(PREF_IS_TR_CONFIRMATION_ENABLED)
        editor.remove(PREF_ACCOUNT_SIGNERS_COUNT)
        return editor.commit()
    }
}