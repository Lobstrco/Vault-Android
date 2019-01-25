package com.lobstr.stellar.vault.presentation.dagger.module.mnemonics

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.mnemonics.MnemonicsInteractor
import com.lobstr.stellar.vault.domain.mnemonics.MnemonicsInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.dagger.scope.MnemonicsScope
import dagger.Module
import dagger.Provides


@Module
class MnemonicsModule {

    @Provides
    @MnemonicsScope
    internal fun provideMnemonicsInteractor(
        keyStoreRepository: KeyStoreRepository,
        stellarRepository: StellarRepository
    ): MnemonicsInteractor {
        return MnemonicsInteractorImpl(keyStoreRepository, stellarRepository)
    }
}