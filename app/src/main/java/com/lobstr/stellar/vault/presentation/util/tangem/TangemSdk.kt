package com.lobstr.stellar.vault.presentation.util.tangem

import android.nfc.NfcAdapter
import androidx.fragment.app.FragmentActivity
import com.tangem.TangemSdk
import com.tangem.common.core.Config
import com.tangem.common.nfc.NfcAvailabilityProvider
import com.tangem.common.card.FirmwareVersion
import com.tangem.crypto.bip39.Wordlist
import com.tangem.sdk.extensions.getWordlist
import com.tangem.sdk.extensions.initNfcManager
import com.tangem.sdk.storage.AndroidSecureStorageV2

fun TangemSdk.Companion.customInit(
    activity: FragmentActivity,
    listener: CustomCardManagerDelegate.CustomCardManagerDelegateListener
): TangemSdk {

    val config = Config().apply {
        linkedTerminal = false
        filter.allowedCardTypes = FirmwareVersion.FirmwareType.entries
    }

    val nfcManager = TangemSdk.initNfcManager(activity)

    val viewDelegate = CustomCardManagerDelegate(activity).apply {
        this.listener = listener
        this.sdkConfig = config
    }

    val nfcAvailabilityProvider = object : NfcAvailabilityProvider {
        override fun isNfcFeatureAvailable(): Boolean {
            val adapter = NfcAdapter.getDefaultAdapter(activity)
            return adapter != null && adapter.isEnabled
        }
    }

    return TangemSdk(
        reader = nfcManager.reader,
        viewDelegate = viewDelegate,
        secureStorage = AndroidSecureStorageV2(
            appContext = activity.applicationContext,
            useStrongBox = true,
        ),
        wordlist = Wordlist.getWordlist(),
        config = config,
        nfcAvailabilityProvider = nfcAvailabilityProvider
    )
}
