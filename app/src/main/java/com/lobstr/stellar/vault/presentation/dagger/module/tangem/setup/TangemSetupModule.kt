package com.lobstr.stellar.vault.presentation.dagger.module.tangem.setup

import com.lobstr.stellar.vault.domain.tangem.setup.TangemSetupInteractor
import com.lobstr.stellar.vault.domain.tangem.setup.TangemSetupInteractorImpl
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TangemSetupModule {
    @Provides
    fun provideTangemSetupInteractor(
        prefsUtil: PrefsUtil
    ): TangemSetupInteractor {
        return TangemSetupInteractorImpl(prefsUtil)
    }
}