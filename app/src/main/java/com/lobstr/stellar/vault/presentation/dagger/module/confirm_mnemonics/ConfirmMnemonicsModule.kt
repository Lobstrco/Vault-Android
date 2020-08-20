package com.lobstr.stellar.vault.presentation.dagger.module.confirm_mnemonics

import com.lobstr.stellar.vault.domain.confirm_mnemonics.ConfirmMnemonicsInteractor
import com.lobstr.stellar.vault.domain.confirm_mnemonics.ConfirmMnemonicsInteractorImpl
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic.ConfirmMnemonicsPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object ConfirmMnemonicsModule {

    @Provides
    fun provideConfirmMnemonicsPresenter(
        confirmMnemonicsInteractor: ConfirmMnemonicsInteractor
    ): ConfirmMnemonicsPresenter {
        return ConfirmMnemonicsPresenter(confirmMnemonicsInteractor)
    }

    @Provides
    fun provideConfirmMnemonicsInteractor(
        stellarRepository: StellarRepository,
        keyStoreRepository: KeyStoreRepository,
        prefsUtil: PrefsUtil
    ): ConfirmMnemonicsInteractor {
        return ConfirmMnemonicsInteractorImpl(stellarRepository, keyStoreRepository, prefsUtil)
    }
}