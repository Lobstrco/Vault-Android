package com.lobstr.stellar.vault.domain.splash


interface SplashInteractor {

    fun hasMnemonics(): Boolean

    fun hasEncryptedPin(): Boolean

    fun hasPublicKey(): Boolean

    fun hasAuthToken(): Boolean

    fun hasSigners(): Boolean

    fun clearUserData()
}