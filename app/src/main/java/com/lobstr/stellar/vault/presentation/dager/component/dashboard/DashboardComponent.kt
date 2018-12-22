package com.lobstr.stellar.vault.presentation.dager.component.dashboard

import com.lobstr.stellar.vault.presentation.dager.module.dashboard.DashboardModule
import com.lobstr.stellar.vault.presentation.dager.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.dashboard.DashboardPresenter
import dagger.Subcomponent

@Subcomponent(modules = [DashboardModule::class])
@HomeScope
interface DashboardComponent {
    fun inject(presenter: DashboardPresenter)
}