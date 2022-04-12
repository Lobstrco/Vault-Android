package com.lobstr.stellar.vault.domain.settings

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.DISABLED
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.ENABLED
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class SettingsInteractorImpl(
    private val prefsUtil: PrefsUtil,
    private val accountRepository: AccountRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val localDataRepository: LocalDataRepository,
    private val fcmHelper: FcmHelper
) : SettingsInteractor {

    override fun getUserPublicKey(): String = prefsUtil.publicKey ?: ""

    override fun getSignersCount(): Int = prefsUtil.accountSignersCount

    override fun getAccountConfig(): Single<AccountConfig> =
        accountRepository.getAccountConfig(AppUtil.getJwtToken(prefsUtil.authToken))

    override fun updatedAccountConfig(spamProtectionEnabled: Boolean): Single<AccountConfig> =
        accountRepository.updateAccountConfig(
            AppUtil.getJwtToken(prefsUtil.authToken),
            spamProtectionEnabled
        )

    override fun hasMnemonics(): Boolean = !prefsUtil.encryptedPhrases.isNullOrEmpty()

    override fun clearUserData() {
        fcmHelper.unregisterFcm()
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clearAll()
        localDataRepository.clearData()
    }

    override fun isBiometricEnabled(): Boolean = prefsUtil.biometricState == ENABLED

    override fun isSpamProtectionEnabled(): Boolean = prefsUtil.isSpamProtectionEnabled

    override fun isNotificationsEnabled(): Boolean =
        localDataRepository.getNotificationInfo(prefsUtil.publicKey!!)

    override fun isTrConfirmationEnabled(): Boolean =
        localDataRepository.getTransactionConfirmationData()[getUserPublicKey()] ?: true

    override fun setBiometricEnabled(enabled: Boolean) {
        prefsUtil.biometricState = if (enabled) ENABLED else DISABLED
    }

    override fun setSpamProtectionEnabled(enabled: Boolean) {
        prefsUtil.isSpamProtectionEnabled = enabled
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        localDataRepository.saveNotificationInfo(prefsUtil.publicKey!!, enabled)
    }

    override fun getSignedAccounts(): Single<List<Account>> =
        accountRepository.getSignedAccounts(AppUtil.getJwtToken(prefsUtil.authToken))
            .doOnSuccess { prefsUtil.accountSignersCount = it.size }

    /**
     * @see Constant.RateUsState
     */
    override fun setRateUsState(state: Int) {
        prefsUtil.rateUsState = state
    }
}