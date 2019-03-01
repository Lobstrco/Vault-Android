package com.lobstr.stellar.vault.domain.rate_us

import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.PrefsUtil


class RateUsInteractorImpl(private val prefsUtil: PrefsUtil) : RateUsInteractor {

    /**
     * @see Constant.RateUsState
     */
    override fun setRateUsState(state: Int) {
        prefsUtil.rateUsState = state
    }
}