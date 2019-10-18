package com.lobstr.stellar.vault.presentation.dagger.component.rate_us

import com.lobstr.stellar.vault.presentation.dagger.module.rate_us.RateUsModule
import com.lobstr.stellar.vault.presentation.dagger.scope.HomeScope
import com.lobstr.stellar.vault.presentation.home.rate_us.RateUsPresenter
import dagger.Subcomponent

@Subcomponent(modules = [RateUsModule::class])
@HomeScope
interface RateUsComponent {
    fun inject(presenter: RateUsPresenter)
}