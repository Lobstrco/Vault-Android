package com.lobstr.stellar.vault.domain.home

interface HomeInteractor {

    fun hasPublicKey(): Boolean

    fun checkFcmRegistration()

    fun isNotificationsEnabled(): Boolean

    fun setNotificationsEnabled(enabled: Boolean)

    fun getRateUsState(): Int
}