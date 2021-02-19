package com.lobstr.stellar.vault.presentation.dagger.module.tangem

import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.tangem.TangemInteractor
import com.lobstr.stellar.vault.domain.tangem.TangemInteractorImpl
import com.lobstr.stellar.vault.domain.tangem.TangemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TangemModule {
    @Provides
    fun provideTangemInteractor(
        stellarRepository: StellarRepository, tangemRepository: TangemRepository
    ): TangemInteractor {
        return TangemInteractorImpl(stellarRepository, tangemRepository)
    }
}