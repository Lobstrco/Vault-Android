package com.lobstr.stellar.vault.presentation.dagger.module.account_name.add

import com.lobstr.stellar.vault.domain.account_name.add.AddAccountNameInteractor
import com.lobstr.stellar.vault.domain.account_name.add.AddAccountNameInteractorImpl
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object AddAccountNameModule {
    @Provides
    fun provideAddAccountNameInteractor(
        localDataRepository: LocalDataRepository
    ): AddAccountNameInteractor = AddAccountNameInteractorImpl(localDataRepository)
}