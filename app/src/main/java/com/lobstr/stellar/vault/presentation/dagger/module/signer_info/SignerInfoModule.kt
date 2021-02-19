package com.lobstr.stellar.vault.presentation.dagger.module.signer_info

import com.lobstr.stellar.vault.domain.signer_info.SignerInfoInteractor
import com.lobstr.stellar.vault.domain.signer_info.SignerInfoInteractorImpl
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object SignerInfoModule {
    @Provides
    fun provideSignerInfoInteractor(
        prefsUtil: PrefsUtil
    ): SignerInfoInteractor {
        return SignerInfoInteractorImpl(
            prefsUtil
        )
    }
}