package com.lobstr.stellar.vault.presentation.dagger.component.import_xdr

import com.lobstr.stellar.vault.presentation.dagger.module.import_xdr.ImportXdrModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.transactions.import_xdr.ImportXdrPresenter
import dagger.Subcomponent

@Subcomponent(modules = [ImportXdrModule::class])
@HomeScope
interface ImportXdrComponent {
    fun inject(presenter: ImportXdrPresenter)
}