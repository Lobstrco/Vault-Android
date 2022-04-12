package com.lobstr.stellar.vault.presentation.util

import android.content.SharedPreferences
import android.util.Base64
import com.lobstr.stellar.vault.presentation.util.Constant.Counter.APP_UPDATE


class PrefsUtil(private val sharedPreferences: SharedPreferences) {

    internal companion object {
        const val PREF_AUTH_TOKEN = "PREF_AUTH_TOKEN"
        const val PREF_PUBLIC_KEY = "PREF_PUBLIC_KEY"
        const val PREF_PUBLIC_KEY_DATA_LIST = "PREF_PUBLIC_KEY_DATA_LIST"
        const val PREF_ENCRYPTED_PHRASES = "PREF_ENCRYPTED_PHRASES"
        const val PREF_ENCRYPTED_PIN = "PREF_ENCRYPTED_PIN"
        const val PREF_PHRASES_IV = "PREF_PHRASES_IV"
        const val PREF_PIN_IV = "PREF_PIN_IV"
        const val PREF_FCM_TOKEN = "PREF_FCM_TOKEN"
        const val PREF_ACCOUNT_HAS_SIGNERS = "PREF_ACCOUNT_HAS_SIGNERS"
        const val PREF_BIOMETRIC_STATE = "PREF_BIOMETRIC_STATE"
        const val PREF_RATE_US_STATE = "PREF_RATE_US_STATE"
        const val PREF_ACCOUNT_SIGNERS_COUNT = "PREF_ACCOUNT_SIGNERS_COUNT"
        const val PREF_IS_SPAM_PROTECTION_ENABLED = "PREF_IS_SPAM_PROTECTION_ENABLED"
        const val PREF_TANGEM_CARD_ID = "PREF_TANGEM_CARD_ID"
        const val PREF_APP_UPDATE_COUNTER_TIMER = "PREF_APP_UPDATE_COUNTER_TIMER"
        const val PREF_APP_UPDATE_RECOMMENDED_STATE = "PREF_APP_UPDATE_RECOMMENDED_STATE"
    }

    var authToken: String?
        get() = getString(PREF_AUTH_TOKEN)
        set(authToken) = set(PREF_AUTH_TOKEN, authToken)

    var publicKey: String?
        get() = getString(PREF_PUBLIC_KEY)
        set(publicKey) = set(PREF_PUBLIC_KEY, publicKey)

    private var publicKeyDataList: String?
        get() = getString(PREF_PUBLIC_KEY_DATA_LIST)
        set(publicKeyList) = set(PREF_PUBLIC_KEY_DATA_LIST, publicKeyList)

    /**
     * @return list with data: Pair<PublicKey,Index>
     */
    fun getPublicKeyDataList(): List<Pair<String, Int>> = when {
        publicKeyDataList.isNullOrEmpty() && publicKey.isNullOrEmpty() -> emptyList()
        publicKeyDataList.isNullOrEmpty() && !publicKey.isNullOrEmpty() -> {
            savePublicKeyToList(publicKey!!, 0)
            listOf(Pair(publicKey!!, 0))
        }
        else -> publicKeyDataList!!.split("#").map {
            val data = it.split(":")
            Pair(data[0], data[1].toInt())
        }
    }

    fun getPublicKeyList(): List<String> =
        getPublicKeyDataList().sortedBy { it.second }.map { it.first }

    fun getPublicKeyIndex(key: String?): Int =
        getPublicKeyDataList().find { it.first == key }.let { it?.second ?: 0 }

    fun getCurrentPublicKeyIndex(): Int = getPublicKeyIndex(publicKey)

    fun getNewPublicKeyIndex(): Int {
        val createdIndexes = getPublicKeyDataList().map { it.second }
        (0 until Constant.Util.PUBLIC_KEY_LIMIT).forEach { newIndex ->
            if (createdIndexes.none { newIndex == it }) {
                return newIndex
            }
        }

        return 0
    }

    fun savePublicKeyToList(key: String, index: Int) {
        if (!publicKeyDataList.isNullOrEmpty() && getPublicKeyDataList().any { it.first == key }) {
            return
        }

        publicKeyDataList = when {
            publicKeyDataList.isNullOrEmpty() -> "$key:$index"
            else -> "$publicKeyDataList#$key:$index"
        }
    }

    var tangemCardId: String?
        get() = getString(PREF_TANGEM_CARD_ID)
        set(publicKey) = set(PREF_TANGEM_CARD_ID, publicKey)

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

    var isSpamProtectionEnabled: Boolean
        get() = getBoolean(PREF_IS_SPAM_PROTECTION_ENABLED)
        set(enabled) = set(PREF_IS_SPAM_PROTECTION_ENABLED, enabled)

    var accountSignersCount: Int
        get() = getInt(PREF_ACCOUNT_SIGNERS_COUNT)
        set(count) = set(PREF_ACCOUNT_SIGNERS_COUNT, count)

    var appUpdateCounterTimer: Int
        get() = getInt(PREF_APP_UPDATE_COUNTER_TIMER)
        set(count) = set(PREF_APP_UPDATE_COUNTER_TIMER, count)

    /**
     * Used for showing App Update Alert for 'between recommended and current state'.
     */
    var appUpdateRecommendedState: Boolean
        get() = getBoolean(PREF_APP_UPDATE_RECOMMENDED_STATE)
        set(enabled) = set(PREF_APP_UPDATE_RECOMMENDED_STATE, enabled)

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

        // Set default value for specific cases.
        when (key) {
            PREF_APP_UPDATE_RECOMMENDED_STATE -> devValue = true
        }

        return sharedPreferences.getBoolean(key, devValue)
    }

    fun getInt(key: String): Int {
        var devValue = 0

        // Set default value for specific cases.
        when (key) {
            PREF_APP_UPDATE_COUNTER_TIMER -> devValue = APP_UPDATE
        }

        return sharedPreferences.getInt(key, devValue)
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, 0)
    }

    /**
     * Clear user authentication prefs exclude some keys.
     */
    fun clearUserPrefs(): Boolean {
        val editor = sharedPreferences.edit()
        for (key in sharedPreferences.all.keys) {
            when (key) {
                // Put here excluded keys.
                PREF_RATE_US_STATE,
                PREF_APP_UPDATE_COUNTER_TIMER,
                PREF_APP_UPDATE_RECOMMENDED_STATE -> {
                    /* do nothing*/
                }
                else -> editor.remove(key)
            }
        }

        return editor.commit()
    }
}