package com.lobstr.stellar.vault.presentation.dagger.component.signed_account

import com.lobstr.stellar.vault.presentation.dagger.module.signed_account.SignedAccountModule
import com.lobstr.stellar.vault.presentation.dagger.scope.SignedAccountScope
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.SignedAccountsPresenter
import dagger.Subcomponent

@Subcomponent(modules = [SignedAccountModule::class])
@SignedAccountScope
interface SignedAccountComponent {
    fun inject(presenter: SignedAccountsPresenter)
}