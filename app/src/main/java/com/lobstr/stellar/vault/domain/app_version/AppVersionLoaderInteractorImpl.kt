package com.lobstr.stellar.vault.domain.app_version

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.presentation.entities.account.AppVersion
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single


class AppVersionLoaderInteractorImpl(
    private val prefsUtil: PrefsUtil,
    private val accountRepository: AccountRepository
) : AppVersionLoaderInteractor {

    override fun setAppUpdatedCounterTimer(count: Int) {
        prefsUtil.appUpdateCounterTimer = count
    }

    override fun getAppUpdatedCounterTimer(): Int {
        return prefsUtil.appUpdateCounterTimer
    }

    override fun getAppVersion(): Single<AppVersion> {
        return accountRepository.getAppVersion()
    }

    override fun setAppUpdateRecommendedState(enabled: Boolean) {
        prefsUtil.appUpdateRecommendedState = enabled
    }

    override fun getAppUpdateRecommendedState(): Boolean {
        return prefsUtil.appUpdateRecommendedState
    }

    override fun isUserAuthorized(): Boolean {
        return when {
            !prefsUtil.publicKey.isNullOrEmpty() -> {
                when {
                    !prefsUtil.encryptedPhrases.isNullOrEmpty() && prefsUtil.encryptedPin.isNullOrEmpty() -> {
                        false
                    }
                    else -> {
                        true
                    }
                }
            }
            else -> {
                false
            }
        }
    }
}