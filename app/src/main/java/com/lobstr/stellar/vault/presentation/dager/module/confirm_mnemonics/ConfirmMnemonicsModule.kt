package com.lobstr.stellar.vault.presentation.dager.module.confirm_mnemonics

import com.lobstr.stellar.vault.domain.confirm_mnemonics.ConfirmMnemonicsInteractor
import com.lobstr.stellar.vault.domain.confirm_mnemonics.ConfirmMnemonicsInteractorImpl
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.dager.scope.AuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class ConfirmMnemonicsModule {

    @Provides
    @AuthScope
    internal fun provideCreateMnemonicsInteractor(
        stellarRepository: StellarRepository,
        prefsUtil: PrefsUtil
    ): ConfirmMnemonicsInteractor {
        return ConfirmMnemonicsInteractorImpl(stellarRepository, prefsUtil)
    }
}