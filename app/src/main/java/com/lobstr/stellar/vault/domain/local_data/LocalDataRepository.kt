package com.lobstr.stellar.vault.domain.local_data

interface LocalDataRepository {

    fun getAccountNames(): MutableMap<String, String?>

    fun saveAccountName(publicKey: String, name: String?)

    fun getNotificationsInfo(): MutableMap<String, Boolean>

    fun getNotificationInfo(key: String): Boolean

    fun saveNotificationInfo(publicKey: String, enabled: Boolean)

    fun getAuthTokens(): MutableMap<String, String>

    fun getAuthToken(key: String): String

    fun saveAuthToken(publicKey: String, token: String)

    fun getFcmRegisteredData(): MutableMap<String, Boolean>

    fun saveIsFcmRegistered(publicKey: String, registered: Boolean)

    fun getTransactionConfirmationData(): MutableMap<String, Boolean>

    fun saveTransactionConfirmation(publicKey: String, enabled: Boolean)

    fun clearData()
}