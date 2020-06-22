package com.lobstr.stellar.vault.presentation.dagger.component.base

import com.lobstr.stellar.vault.presentation.base.activity.BaseActivityPresenter
import com.lobstr.stellar.vault.presentation.dagger.module.base.BaseModule
import com.lobstr.stellar.vault.presentation.dagger.scope.BaseScope
import dagger.Subcomponent

@Subcomponent(modules = [BaseModule::class])
@BaseScope
interface BaseComponent {
    fun inject(presenter: BaseActivityPresenter)
}