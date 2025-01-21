package com.lobstr.stellar.vault.domain.home

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil

class HomeInteractorImpl(
    private val prefsUtil: PrefsUtil,
    private val localDataRepository: LocalDataRepository,
    private val fcmHelper: FcmHelper,
) : HomeInteractor {

    override fun hasPublicKey(): Boolean {
        return !prefsUtil.publicKey.isNullOrEmpty()
    }

    override fun checkFcmRegistration() {
        fcmHelper.checkFcmRegistration()
    }

    override fun isNotificationsEnabled(): Boolean =
        localDataRepository.getNotificationInfo(prefsUtil.publicKey!!)

    override fun setNotificationsEnabled(enabled: Boolean) {
        localDataRepository.saveNotificationInfo(prefsUtil.publicKey!!, enabled)
    }

    override fun getRateUsState(): Int {
        return prefsUtil.rateUsState
    }
}