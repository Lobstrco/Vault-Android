package com.lobstr.stellar.vault.domain.config

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class ConfigInteractorImpl(
    private val accountRepository: AccountRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil
) : ConfigInteractor {

    override fun isTrConfirmationEnabled(): Boolean =
        localDataRepository.getTransactionConfirmationData()[getUserPublicKey()] ?: true

    override fun isSpamProtectionEnabled(): Boolean = prefsUtil.isSpamProtectionEnabled

    override fun setSpamProtectionEnabled(enabled: Boolean) {
        prefsUtil.isSpamProtectionEnabled = enabled
    }

    override fun setTrConfirmationEnabled(enabled: Boolean) {
        localDataRepository.saveTransactionConfirmation(getUserPublicKey(), enabled)
    }

    override fun updatedAccountConfig(spamProtectionEnabled: Boolean): Single<AccountConfig> =
        accountRepository.updateAccountConfig(
            AppUtil.getJwtToken(prefsUtil.authToken),
            spamProtectionEnabled
        )

    override fun getUserPublicKey(): String = prefsUtil.publicKey ?: ""
}