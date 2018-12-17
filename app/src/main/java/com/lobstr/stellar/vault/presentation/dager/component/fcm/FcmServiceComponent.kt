package com.lobstr.stellar.vault.presentation.dager.component.fcm

import com.lobstr.stellar.vault.presentation.dager.module.fcm.FcmServiceModule
import com.lobstr.stellar.vault.presentation.dager.scope.FcmServiceScope
import com.lobstr.stellar.vault.presentation.fcm.LVFirebaseMessagingService
import dagger.Subcomponent


@Subcomponent(modules = [FcmServiceModule::class])
@FcmServiceScope
interface FcmServiceComponent {
    fun inject(lvFirebaseMessagingService: LVFirebaseMessagingService)
}
