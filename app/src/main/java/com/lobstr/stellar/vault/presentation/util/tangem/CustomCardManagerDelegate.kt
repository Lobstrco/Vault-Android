package com.lobstr.stellar.vault.presentation.util.tangem

import android.app.Activity
import com.tangem.Message
import com.tangem.SessionViewDelegate
import com.tangem.WrongValueType
import com.tangem.common.UserCodeType
import com.tangem.common.core.CompletionCallback
import com.tangem.common.core.Config
import com.tangem.common.core.TangemError
import com.tangem.common.extensions.VoidCallback
import com.tangem.operations.resetcode.ResetCodesViewDelegate
import com.tangem.tangem_sdk_new.AndroidResetCodesViewDelegate

class CustomCardManagerDelegate(
    private val activity: Activity
) : SessionViewDelegate {
    lateinit var listener: CustomCardManagerDelegateListener
    var sdkConfig: Config? = null

    override val resetCodesViewDelegate: ResetCodesViewDelegate =
        AndroidResetCodesViewDelegate(activity)

    override fun attestationCompletedOffline(
        positive: VoidCallback,
        negative: VoidCallback,
        retry: VoidCallback
    ) {

    }

    override fun attestationCompletedWithWarnings(positive: VoidCallback) {

    }

    override fun attestationDidFail(
        isDevCard: Boolean,
        positive: VoidCallback,
        negative: VoidCallback
    ) {

    }

    override fun dismiss() {

    }

    override fun onDelay(total: Int, current: Int, step: Int) {

    }

    override fun onError(error: TangemError) {

    }

    override fun onSecurityDelay(ms: Int, totalDurationSeconds: Int) {
        listener.onSecurityDelay(ms, totalDurationSeconds)
    }

    override fun onSessionStarted(cardId: String?, message: Message?, enableHowTo: Boolean) {
        listener.onSessionStarted()
    }

    override fun onSessionStopped(message: Message?) {
        listener.onSessionStopped()
    }

    override fun onTagConnected() {
        listener.onTagConnected()
    }

    override fun onTagLost() {
        listener.onTagLost()
    }

    override fun onWrongCard(wrongValueType: WrongValueType) {
        listener.onWrongCard()
    }

    override fun requestUserCode(
        type: UserCodeType,
        isFirstAttempt: Boolean,
        showForgotButton: Boolean,
        cardId: String?,
        callback: CompletionCallback<String>
    ) {

    }

    override fun requestUserCodeChange(
        type: UserCodeType,
        cardId: String?,
        callback: CompletionCallback<String>
    ) {

    }

    override fun setConfig(config: Config) {

    }

    override fun setMessage(message: Message?) {

    }

    interface CustomCardManagerDelegateListener {
        fun onSessionStarted()
        fun onSessionStopped()
        fun onTagConnected()
        fun onTagLost()
        fun onLostCard()
        fun onWrongCard()
        fun onSecurityDelay(ms: Int, totalDurationSeconds: Int)
    }
}