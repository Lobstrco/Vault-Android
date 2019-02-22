package com.lobstr.stellar.vault.presentation.dagger.component.home

import com.lobstr.stellar.vault.presentation.dagger.module.home.HomeModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.HomeActivityPresenter
import dagger.Subcomponent


@Subcomponent(modules = [HomeModule::class])
@HomeScope
interface HomeComponent {
    fun inject(presenter: HomeActivityPresenter)
}