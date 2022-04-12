package com.lobstr.stellar.vault.presentation.dagger.module.base

import com.lobstr.stellar.vault.domain.base.BaseInteractor
import com.lobstr.stellar.vault.domain.base.BaseInteractorImpl
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.vault_auth.VaultAuthRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object BaseModule {
    @Provides
    fun provideBaseInteractor(
        vaultAuthRepository: VaultAuthRepository,
        localDataRepository: LocalDataRepository,
        prefsUtil: PrefsUtil
    ): BaseInteractor = BaseInteractorImpl(vaultAuthRepository, localDataRepository, prefsUtil)
}