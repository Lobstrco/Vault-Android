package com.lobstr.stellar.vault.domain.home

import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class HomeInteractorImpl(private val prefsUtil: PrefsUtil, private val fcmHelper: FcmHelper) : HomeInteractor {

    override fun checkFcmRegistration() {
        fcmHelper.checkFcmRegistration()
    }

    override fun getRateUsState(): Int {
        return prefsUtil.rateUsState
    }
}