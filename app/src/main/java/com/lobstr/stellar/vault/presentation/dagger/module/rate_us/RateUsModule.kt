package com.lobstr.stellar.vault.presentation.dagger.module.rate_us

import com.lobstr.stellar.vault.domain.rate_us.RateUsInteractor
import com.lobstr.stellar.vault.domain.rate_us.RateUsInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class RateUsModule {
    @Provides
    @HomeScope
    internal fun provideRateUsInteractor(prefsUtil: PrefsUtil): RateUsInteractor {
        return RateUsInteractorImpl(prefsUtil)
    }
}