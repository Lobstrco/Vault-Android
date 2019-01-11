package com.lobstr.stellar.vault.presentation.dagger.module.touch_id

import com.lobstr.stellar.vault.domain.touch_id.FingerprintSetUpInteractor
import com.lobstr.stellar.vault.domain.touch_id.FingerprintSetUpInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides


@Module
class FingerprintSetUpModule {
    @Provides
    @AuthScope
    internal fun provideFingerprintSetUpInteractor(
        prefsUtil: PrefsUtil
    ): FingerprintSetUpInteractor {
        return FingerprintSetUpInteractorImpl(prefsUtil)
    }
}