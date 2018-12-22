package com.lobstr.stellar.vault.presentation.dager.component.fcm

import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractorImpl
import com.lobstr.stellar.vault.presentation.dager.module.fcm.FcmInternalModule
import com.lobstr.stellar.vault.presentation.dager.scope.FcmInternalScope
import dagger.Subcomponent

@Subcomponent(modules = [FcmInternalModule::class])
@FcmInternalScope
interface FcmInternalComponent {
    fun inject(vaultAuthInteractorImpl: VaultAuthInteractorImpl)
}