package com.lobstr.stellar.vault.presentation.dagger.module.suggest_rate_us

import com.lobstr.stellar.vault.domain.suggest_rate_us.SuggestRateUsInteractor
import com.lobstr.stellar.vault.domain.suggest_rate_us.SuggestRateUsInteractorImpl
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object SuggestRateUsModule {
    @Provides
    fun provideSuggestRateUsInteractor(prefsUtil: PrefsUtil): SuggestRateUsInteractor {
        return SuggestRateUsInteractorImpl(prefsUtil)
    }
}