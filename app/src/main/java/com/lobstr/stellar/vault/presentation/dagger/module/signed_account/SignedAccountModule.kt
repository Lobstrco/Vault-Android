package com.lobstr.stellar.vault.presentation.dagger.module.signed_account

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractor
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractorImpl
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.SignedAccountsPresenter
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object SignedAccountModule {

    @Provides
    fun provideSignedAccountsPresenter(
        signedAccountInteractor: SignedAccountInteractor,
        eventProviderModule: EventProviderModule
    ): SignedAccountsPresenter {
        return SignedAccountsPresenter(
            signedAccountInteractor,
            eventProviderModule
        )
    }

    @Provides
    fun provideSignedAccountInteractor(
        accountRepository: AccountRepository,
        prefsUtil: PrefsUtil
    ): SignedAccountInteractor {
        return SignedAccountInteractorImpl(accountRepository, prefsUtil)
    }
}