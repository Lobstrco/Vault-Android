package com.lobstr.stellar.vault.domain.settings

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.Single

interface SettingsInteractor {

    fun clearUserData()

    fun getUserPublicKey(): String?

    fun getSignersCount(): Int

    fun isTouchIdEnabled(): Boolean

    fun setTouchIdEnabled(enabled: Boolean)

    fun getSignedAccounts(): Single<List<Account>>
}