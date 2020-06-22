package com.lobstr.stellar.vault.presentation.util.tangem

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.tangem.TangemSdk
import com.tangem.tangem_sdk_new.NfcLifecycleObserver
import com.tangem.tangem_sdk_new.TerminalKeysStorage
import com.tangem.tangem_sdk_new.nfc.NfcManager

fun TangemSdk.Companion.customInit(
    lifecycle: Lifecycle,
    activity: FragmentActivity,
    listener: CustomCardManagerDelegate.CustomCardManagerDelegateListener
): TangemSdk {
    val nfcManager = NfcManager().apply {
        this.setCurrentActivity(activity)
        lifecycle.addObserver(NfcLifecycleObserver(this))
    }

    val viewDelegate = CustomCardManagerDelegate(nfcManager.reader).apply {
        this.listener = listener
    }

    return TangemSdk(nfcManager.reader, viewDelegate).apply {
        this.setTerminalKeysService(TerminalKeysStorage(activity.application))
    }
}