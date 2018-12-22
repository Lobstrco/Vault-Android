package com.lobstr.stellar.vault.domain.settings


interface SettingsInteractor {
    fun clearUserData()

    fun getUserPublicKey(): String?

    fun getSignedAccount(): String?
}