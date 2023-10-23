package com.lobstr.stellar.vault.presentation.util.tangem

import androidx.fragment.app.FragmentActivity
import com.tangem.TangemSdk
import com.tangem.common.card.FirmwareVersion
import com.tangem.common.core.Config
import com.tangem.common.services.secure.SecureStorage
import com.tangem.tangem_sdk_new.extensions.initNfcManager
import com.tangem.tangem_sdk_new.storage.create

fun TangemSdk.Companion.customInit(
    activity: FragmentActivity,
    listener: CustomCardManagerDelegate.CustomCardManagerDelegateListener
): TangemSdk {

    val config = Config().apply {
        linkedTerminal = false
        allowUntrustedCards = true
        filter.allowedCardTypes = FirmwareVersion.FirmwareType.entries
    }

    val nfcManager = TangemSdk.initNfcManager(activity)

    val viewDelegate = CustomCardManagerDelegate(activity).apply {
        this.listener = listener
        this.sdkConfig = config
    }

    return TangemSdk(
        reader = nfcManager.reader,
        viewDelegate = viewDelegate,
        secureStorage = SecureStorage.create(activity),
        config = config
    )
}