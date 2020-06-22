package com.lobstr.stellar.vault.presentation.dagger.component.tangem.setup

import com.lobstr.stellar.vault.presentation.auth.tangem.TangemSetupPresenter
import com.lobstr.stellar.vault.presentation.dagger.module.tangem.setup.TangemSetupModule
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import dagger.Subcomponent

@Subcomponent(modules = [TangemSetupModule::class])
@AuthScope
interface TangemSetupComponent {
    fun inject(presenter: TangemSetupPresenter)
}