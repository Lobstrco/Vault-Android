package com.lobstr.stellar.vault.presentation.dagger.module.rate_us

import com.lobstr.stellar.vault.domain.rate_us.RateUsInteractor
import com.lobstr.stellar.vault.domain.rate_us.RateUsInteractorImpl
import com.lobstr.stellar.vault.presentation.home.rate_us.RateUsPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object RateUsModule {

    @Provides
    fun provideRateUsPresenter(
        rateUsInteractor: RateUsInteractor
    ): RateUsPresenter {
        return RateUsPresenter(rateUsInteractor)
    }

    @Provides
    fun provideRateUsInteractor(prefsUtil: PrefsUtil): RateUsInteractor {
        return RateUsInteractorImpl(prefsUtil)
    }
}