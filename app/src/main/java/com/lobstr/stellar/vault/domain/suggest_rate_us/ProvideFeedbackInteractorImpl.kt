package com.lobstr.stellar.vault.domain.suggest_rate_us

import com.lobstr.stellar.vault.presentation.util.PrefsUtil

class ProvideFeedbackInteractorImpl(private val prefsUtil: PrefsUtil) : ProvideFeedbackInteractor {

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }
}