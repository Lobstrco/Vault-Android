package com.lobstr.stellar.vault.presentation.dagger.component.vault_auth

import com.lobstr.stellar.vault.presentation.dagger.module.vault_auth.VaultAuthModule
import com.lobstr.stellar.vault.presentation.dagger.scope.VaultAuthScope
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthPresenter
import com.lobstr.stellar.vault.presentation.vault_auth.auth.VaultAuthFrPresenter
import dagger.Subcomponent

@Subcomponent(modules = [VaultAuthModule::class])
@VaultAuthScope
interface VaultAuthComponent {
    fun inject(presenter: VaultAuthPresenter)
    fun inject(presenter: VaultAuthFrPresenter)
}