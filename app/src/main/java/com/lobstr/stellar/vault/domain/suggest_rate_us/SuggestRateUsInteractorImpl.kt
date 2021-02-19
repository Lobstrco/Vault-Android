package com.lobstr.stellar.vault.domain.suggest_rate_us

import com.lobstr.stellar.vault.presentation.util.PrefsUtil

class SuggestRateUsInteractorImpl(private val prefsUtil: PrefsUtil) : SuggestRateUsInteractor {

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }
}