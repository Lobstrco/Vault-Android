package com.lobstr.stellar.vault.domain.config

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single


class ConfigInteractorImpl(
    private val accountRepository: AccountRepository,
    private val prefsUtil: PrefsUtil
) : ConfigInteractor {

    override fun isTrConfirmationEnabled(): Boolean {
        return prefsUtil.isTrConfirmationEnabled
    }

    override fun isSpamProtectionEnabled(): Boolean {
        return prefsUtil.isSpamProtectionEnabled
    }

    override fun setSpamProtectionEnabled(enabled: Boolean) {
        prefsUtil.isSpamProtectionEnabled = enabled
    }

    override fun setTrConfirmationEnabled(enabled: Boolean) {
        prefsUtil.isTrConfirmationEnabled = enabled
    }

    override fun updatedAccountConfig(spamProtectionEnabled: Boolean): Single<AccountConfig> {
        return accountRepository.updateAccountConfig(
            AppUtil.getJwtToken(prefsUtil.authToken),
            spamProtectionEnabled
        )
    }

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }
}