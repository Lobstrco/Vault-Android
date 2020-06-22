package com.lobstr.stellar.vault.domain.home

import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class HomeInteractorImpl(private val prefsUtil: PrefsUtil) : HomeInteractor {

    override fun checkFcmRegistration() {
        LVApplication.appComponent.fcmHelper.checkFcmRegistration()
    }

    override fun getRateUsState(): Int {
        return prefsUtil.rateUsState
    }
}