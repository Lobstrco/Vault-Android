package com.lobstr.stellar.vault.presentation.dagger.module.suggest_rate_us

import com.lobstr.stellar.vault.domain.suggest_rate_us.ProvideFeedbackInteractor
import com.lobstr.stellar.vault.domain.suggest_rate_us.ProvideFeedbackInteractorImpl
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object ProvideFeedbackModule {
    @Provides
    fun provideFeedbackInteractor(prefsUtil: PrefsUtil): ProvideFeedbackInteractor {
        return ProvideFeedbackInteractorImpl(prefsUtil)
    }
}