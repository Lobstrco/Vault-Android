package com.lobstr.stellar.vault.presentation.dagger.module.account_name.manage

import com.lobstr.stellar.vault.domain.account_name.manage.ManageAccountsNamesInteractor
import com.lobstr.stellar.vault.domain.account_name.manage.ManageAccountsNamesInteractorImpl
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object ManageAccountsNamesModule {
    @Provides
    fun provideManageAccountsNamesInteractor(
        localDataRepository: LocalDataRepository
    ): ManageAccountsNamesInteractor = ManageAccountsNamesInteractorImpl(localDataRepository)
}