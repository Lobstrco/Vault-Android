package com.lobstr.stellar.vault.domain.settings

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.DISABLED
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.ENABLED
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single

class SettingsInteractorImpl(
    private val prefsUtil: PrefsUtil,
    private val accountRepository: AccountRepository,
    private val keyStoreRepository: KeyStoreRepository
) :
    SettingsInteractor {

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun getSignersCount(): Int {
        return prefsUtil.accountSignersCount
    }

    override fun clearUserData() {
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clearAll()
    }

    override fun isBiometricEnabled(): Boolean {
        return prefsUtil.biometricState == ENABLED
    }

    override fun isNotificationsEnabled(): Boolean {
        return prefsUtil.isNotificationsEnabled
    }

    override fun isTrConfirmationEnabled(): Boolean {
        return prefsUtil.isTrConfirmationEnabled
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        prefsUtil.biometricState = if (enabled) ENABLED else DISABLED
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        prefsUtil.isNotificationsEnabled = enabled
    }

    override fun setTrConfirmationEnabled(enabled: Boolean) {
        prefsUtil.isTrConfirmationEnabled = enabled
    }

    override fun getSignedAccounts(): Single<List<Account>> {
        return accountRepository.getSignedAccounts(AppUtil.getJwtToken(prefsUtil.authToken))
            .doOnSuccess { prefsUtil.accountSignersCount = it.size }
    }
}