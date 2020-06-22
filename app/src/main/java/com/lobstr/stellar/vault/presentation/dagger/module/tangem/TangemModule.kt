package com.lobstr.stellar.vault.presentation.dagger.module.tangem

import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.tangem.TangemInteractor
import com.lobstr.stellar.vault.domain.tangem.TangemInteractorImpl
import com.lobstr.stellar.vault.domain.tangem.TangemRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.TangemScope
import dagger.Module
import dagger.Provides

@Module
class TangemModule {
    @Provides
    @TangemScope
    internal fun provideTangemInteractor(
        stellarRepository: StellarRepository, tangemRepository: TangemRepository
    ): TangemInteractor {
        return TangemInteractorImpl(stellarRepository, tangemRepository)
    }
}