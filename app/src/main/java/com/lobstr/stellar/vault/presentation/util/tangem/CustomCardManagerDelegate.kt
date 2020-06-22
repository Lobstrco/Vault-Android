package com.lobstr.stellar.vault.presentation.util.tangem

import com.tangem.Message
import com.tangem.SessionViewDelegate
import com.tangem.TangemSdkError
import com.tangem.common.CompletionResult
import com.tangem.tangem_sdk_new.nfc.NfcReader

class CustomCardManagerDelegate(private val reader: NfcReader) : SessionViewDelegate {

    lateinit var listener: CustomCardManagerDelegateListener

    override fun onNfcSessionStarted(cardId: String?, message: Message?) {

    }

    override fun onDelay(total: Int, current: Int, step: Int) {
    }

    override fun onError(error: TangemSdkError) {

    }

    override fun onNfcSessionCompleted(message: Message?) {

    }

    override fun onPinRequested(callback: (result: CompletionResult<String>) -> Unit) {

    }

    override fun onSecurityDelay(ms: Int, totalDurationSeconds: Int) {
    }

    override fun onTagLost() {
        listener.onLostCard()
    }

    interface CustomCardManagerDelegateListener {
        fun onLostCard()
    }
}