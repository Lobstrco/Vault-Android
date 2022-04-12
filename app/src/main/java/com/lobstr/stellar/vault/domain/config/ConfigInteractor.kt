package com.lobstr.stellar.vault.domain.config

import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig
import io.reactivex.rxjava3.core.Single


interface ConfigInteractor {

    fun isTrConfirmationEnabled(): Boolean

    fun isSpamProtectionEnabled(): Boolean

    fun setSpamProtectionEnabled(enabled: Boolean)

    fun setTrConfirmationEnabled(enabled: Boolean)

    fun updatedAccountConfig(spamProtectionEnabled: Boolean): Single<AccountConfig>

    fun getUserPublicKey(): String
}