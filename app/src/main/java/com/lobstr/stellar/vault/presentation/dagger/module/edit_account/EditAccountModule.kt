package com.lobstr.stellar.vault.presentation.dagger.module.edit_account

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.edit_account.EditAccountInteractor
import com.lobstr.stellar.vault.domain.edit_account.EditAccountInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object EditAccountModule {
    @Provides
    fun provideEditAccountInteractor(
        accountRepository: AccountRepository
    ): EditAccountInteractor {
        return EditAccountInteractorImpl(accountRepository)
    }
}