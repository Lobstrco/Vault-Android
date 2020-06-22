package com.lobstr.stellar.vault.presentation.dagger.module.signer_info

import com.lobstr.stellar.vault.domain.signer_info.SignerInfoInteractor
import com.lobstr.stellar.vault.domain.signer_info.SignerInfoInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.scope.VaultAuthScope
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides

@Module
class SignerInfoModule {
    @Provides
    @VaultAuthScope
    internal fun provideSignerInfoInteractor(
        prefsUtil: PrefsUtil
    ): SignerInfoInteractor {
        return SignerInfoInteractorImpl(
            prefsUtil
        )
    }
}