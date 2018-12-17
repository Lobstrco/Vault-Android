package com.lobstr.stellar.vault.presentation.dager.component.fcm

import com.lobstr.stellar.vault.presentation.dager.module.fcm.FcmInternalModule
import com.lobstr.stellar.vault.presentation.dager.scope.FcmInternalScope
import com.lobstr.stellar.vault.presentation.home.HomeActivityPresenter
import dagger.Subcomponent

@Subcomponent(modules = [FcmInternalModule::class])
@FcmInternalScope
interface FcmInternalComponent {
    fun inject(homeActivityPresenter: HomeActivityPresenter)
}