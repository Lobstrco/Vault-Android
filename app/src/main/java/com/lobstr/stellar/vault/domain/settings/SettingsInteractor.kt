package com.lobstr.stellar.vault.domain.settings

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig
import io.reactivex.rxjava3.core.Single

interface SettingsInteractor {

    fun hasMnemonics(): Boolean

    fun clearUserData()

    fun getUserPublicKey(): String?

    fun getSignersCount(): Int

    fun getAccountConfig(): Single<AccountConfig>

    fun updatedAccountConfig(spamProtectionEnabled: Boolean): Single<AccountConfig>

    fun isBiometricEnabled(): Boolean

    fun isSpamProtectionEnabled(): Boolean

    fun isNotificationsEnabled(): Boolean

    fun isTrConfirmationEnabled(): Boolean

    fun setBiometricEnabled(enabled: Boolean)

    fun setSpamProtectionEnabled(enabled: Boolean)

    fun setNotificationsEnabled(enabled: Boolean)

    fun setTrConfirmationEnabled(enabled: Boolean)

    fun getSignedAccounts(): Single<List<Account>>

    fun setRateUsState(state: Int)
}