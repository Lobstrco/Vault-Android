package com.lobstr.stellar.vault.presentation.dagger.component.recovery_key

import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoveryKeyFrPresenter
import com.lobstr.stellar.vault.presentation.dagger.module.recovery_key.RecoveryKeyModule
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import dagger.Subcomponent

@Subcomponent(modules = [RecoveryKeyModule::class])
@AuthScope
interface RecoveryKeyComponent {
    fun inject(presenter: RecoveryKeyFrPresenter)
}