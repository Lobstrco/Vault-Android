package com.lobstr.stellar.vault.presentation.dagger.component.touch_id

import com.lobstr.stellar.vault.presentation.auth.touch_id.FingerprintSetUpPresenter
import com.lobstr.stellar.vault.presentation.dagger.module.touch_id.FingerprintSetUpModule
import com.lobstr.stellar.vault.presentation.dagger.scope.AuthScope
import dagger.Subcomponent

@Subcomponent(modules = [FingerprintSetUpModule::class])
@AuthScope
interface FingerprintSetUpComponent {
    fun inject(presenter: FingerprintSetUpPresenter)
}