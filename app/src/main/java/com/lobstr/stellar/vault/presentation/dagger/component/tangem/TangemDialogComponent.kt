package com.lobstr.stellar.vault.presentation.dagger.component.tangem

import com.lobstr.stellar.vault.presentation.dagger.module.tangem.TangemModule
import com.lobstr.stellar.vault.presentation.dagger.scope.TangemScope
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogPresenter
import dagger.Subcomponent

@Subcomponent(modules = [TangemModule::class])
@TangemScope
interface TangemDialogComponent {
    fun inject(presenter: TangemDialogPresenter)
}