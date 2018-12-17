package com.lobstr.stellar.vault.presentation.dager.component.recovery_key

import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoveryKeyFrPresenter
import com.lobstr.stellar.vault.presentation.dager.module.recovery_key.RecoveryKeyModule
import com.lobstr.stellar.vault.presentation.dager.scope.AuthScope
import dagger.Subcomponent

@Subcomponent(modules = [RecoveryKeyModule::class])
@AuthScope
interface RecoveryKeyComponent {
    fun inject(presenter: RecoveryKeyFrPresenter)
}