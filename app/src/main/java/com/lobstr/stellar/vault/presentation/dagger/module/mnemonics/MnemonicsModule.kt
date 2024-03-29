package com.lobstr.stellar.vault.presentation.dagger.module.mnemonics

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.mnemonics.MnemonicsInteractor
import com.lobstr.stellar.vault.domain.mnemonics.MnemonicsInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent


@Module
@InstallIn(FragmentComponent::class)
object MnemonicsModule {
    @Provides
    fun provideMnemonicsInteractor(
        keyStoreRepository: KeyStoreRepository,
        stellarRepository: StellarRepository
    ): MnemonicsInteractor {
        return MnemonicsInteractorImpl(keyStoreRepository, stellarRepository)
    }
}