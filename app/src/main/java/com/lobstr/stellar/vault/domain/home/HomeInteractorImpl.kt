package com.lobstr.stellar.vault.domain.home

import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.fcm.FcmInternalModule
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import javax.inject.Inject


class HomeInteractorImpl(private val prefsUtil: PrefsUtil) : HomeInteractor {

    @Inject
    lateinit var mFcmHelper: FcmHelper

    init {
        LVApplication.appComponent.plusFcmInternalComponent(FcmInternalModule()).inject(this)
    }

    override fun checkFcmRegistration() {
        mFcmHelper.checkIfFcmRegisteredSuccessfully()
    }

    override fun getRateUsState(): Int {
        return prefsUtil.rateUsState
    }
}