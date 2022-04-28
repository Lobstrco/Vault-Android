package com.lobstr.stellar.vault.presentation.dagger.module.show_public_key

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.show_public_key.ShowPublicKeyInteractor
import com.lobstr.stellar.vault.domain.show_public_key.ShowPublicKeyInteractorImpl
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object ShowPublicKeyModule {
    @Provides
    fun provideShowPublicKeyInteractor(
        localDataRepository: LocalDataRepository,
        prefsUtil: PrefsUtil
    ): ShowPublicKeyInteractor = ShowPublicKeyInteractorImpl(
        localDataRepository,
        prefsUtil
    )
}