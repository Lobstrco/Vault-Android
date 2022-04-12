package com.lobstr.stellar.vault.data.local_data

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil.Companion.ACCOUNT_NAME_STORAGE
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil.Companion.AUTH_TOKEN_STORAGE
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil.Companion.FCM_STORAGE
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil.Companion.NOTIFICATION_STORAGE
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil.Companion.TRANSACTION_CONFIRMATION_STORAGE

class LocalDataRepositoryImpl(
    private val file: FileStreamUtil
) : LocalDataRepository {

    override fun getAccountNames(): MutableMap<String, String?> =
        file.read<MutableMap<String, String?>>(ACCOUNT_NAME_STORAGE) ?: mutableMapOf()

    override fun saveAccountName(publicKey: String, name: String?) {
        file.write(ACCOUNT_NAME_STORAGE, getAccountNames().apply {
            if (name.isNullOrEmpty()) {
                remove(publicKey)
            } else {
                put(publicKey, name)
            }
        })
    }

    override fun getNotificationsInfo(): MutableMap<String, Boolean> =
        file.read<MutableMap<String, Boolean>>(NOTIFICATION_STORAGE) ?: mutableMapOf()

    override fun getNotificationInfo(key: String): Boolean = getNotificationsInfo()[key] ?: true

    override fun saveNotificationInfo(publicKey: String, enabled: Boolean) {
        file.write(NOTIFICATION_STORAGE, getNotificationsInfo().apply {
            if (enabled) {
                remove(publicKey)
            } else {
                put(publicKey, enabled)
            }
        })
    }

    override fun getAuthTokens(): MutableMap<String, String> =
        file.read<MutableMap<String, String>>(AUTH_TOKEN_STORAGE) ?: mutableMapOf()

    override fun getAuthToken(key: String): String = getAuthTokens()[key] ?: ""

    override fun saveAuthToken(publicKey: String, token: String) {
        file.write(AUTH_TOKEN_STORAGE, getAuthTokens().apply {
            if (token.isEmpty()) {
                remove(publicKey)
            } else {
                put(publicKey, token)
            }
        })
    }

    override fun getFcmRegisteredData(): MutableMap<String, Boolean> =
        file.read<MutableMap<String, Boolean>>(FCM_STORAGE) ?: mutableMapOf()

    override fun saveIsFcmRegistered(publicKey: String, registered: Boolean) {
        file.write(FCM_STORAGE, getFcmRegisteredData().apply {
            if (registered) {
                put(publicKey, registered)
            } else {
                remove(publicKey)
            }
        })
    }

    override fun getTransactionConfirmationData(): MutableMap<String, Boolean> =
        file.read<MutableMap<String, Boolean>>(TRANSACTION_CONFIRMATION_STORAGE) ?: mutableMapOf()

    override fun saveTransactionConfirmation(publicKey: String, enabled: Boolean) {
        file.write(TRANSACTION_CONFIRMATION_STORAGE, getFcmRegisteredData().apply {
            if (!enabled) {
                put(publicKey, enabled)
            } else {
                remove(publicKey)
            }
        })
    }

    override fun clearData() {
        file.deleteFile(ACCOUNT_NAME_STORAGE)
        file.deleteFile(TRANSACTION_CONFIRMATION_STORAGE)
        file.deleteFile(NOTIFICATION_STORAGE)
        file.deleteFile(AUTH_TOKEN_STORAGE)
        file.deleteFile(FCM_STORAGE)
    }
}