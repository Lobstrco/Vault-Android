package com.lobstr.stellar.vault.domain.settings


interface SettingsInteractor {

    fun clearUserData()

    fun getUserPublicKey(): String?

    fun getSignersCount(): Int

    fun isTouchIdEnabled(): Boolean

    fun setTouchIdEnabled(enabled: Boolean)
}