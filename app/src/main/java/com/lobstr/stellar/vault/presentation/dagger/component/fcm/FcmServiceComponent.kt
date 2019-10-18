package com.lobstr.stellar.vault.presentation.dagger.component.fcm

import com.lobstr.stellar.vault.presentation.dagger.module.fcm.FcmServiceModule
import com.lobstr.stellar.vault.presentation.dagger.scope.FcmServiceScope
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService
import dagger.Subcomponent


@Subcomponent(modules = [FcmServiceModule::class])
@FcmServiceScope
interface FcmServiceComponent {
    fun inject(lvFirebaseMessagingService: LVFirebaseMessagingService)
}
