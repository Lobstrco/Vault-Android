package com.lobstr.stellar.vault.presentation.dagger.component.fcm

import com.lobstr.stellar.vault.domain.home.HomeInteractorImpl
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractorImpl
import com.lobstr.stellar.vault.presentation.dagger.module.fcm.FcmInternalModule
import com.lobstr.stellar.vault.presentation.dagger.scope.FcmInternalScope
import dagger.Subcomponent

@Subcomponent(modules = [FcmInternalModule::class])
@FcmInternalScope
interface FcmInternalComponent {
    fun inject(vaultAuthInteractorImpl: VaultAuthInteractorImpl)

    fun inject(homeInteractorImpl: HomeInteractorImpl)
}