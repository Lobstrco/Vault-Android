package com.lobstr.stellar.vault.presentation.dagger.module.tangem.setup

import com.lobstr.stellar.vault.domain.tangem.setup.TangemSetupInteractor
import com.lobstr.stellar.vault.domain.tangem.setup.TangemSetupInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class TangemSetupModule {
    @Provides
    @AuthScope
    internal fun provideTangemSetupInteractor(
        prefsUtil: PrefsUtil
    ): TangemSetupInteractor {
        return TangemSetupInteractorImpl(prefsUtil)
    }
}