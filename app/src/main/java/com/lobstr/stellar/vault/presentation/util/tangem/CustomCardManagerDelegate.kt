package com.lobstr.stellar.vault.presentation.util.tangem

import android.app.Activity
import com.tangem.Message
import com.tangem.SessionViewDelegate
import com.tangem.ViewDelegateMessage
import com.tangem.WrongValueType
import com.tangem.common.CompletionResult
import com.tangem.common.UserCodeType
import com.tangem.common.core.CompletionCallback
import com.tangem.common.core.Config
import com.tangem.common.core.ProductType
import com.tangem.common.core.TangemError
import com.tangem.common.core.TangemSdkError
import com.tangem.common.extensions.VoidCallback
import com.tangem.operations.resetcode.ResetCodesViewDelegate
import com.tangem.sdk.AndroidResetCodesViewDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CustomCardManagerDelegate(
    private val activity: Activity
) : SessionViewDelegate {
    lateinit var listener: CustomCardManagerDelegateListener
    var sdkConfig: Config? = null

    private val _viewVisibility = MutableStateFlow(false)
    override val viewVisibility: StateFlow<Boolean> get() = _viewVisibility

    override val resetCodesViewDelegate: ResetCodesViewDelegate =
        AndroidResetCodesViewDelegate(activity)

    override fun attestationCompletedOffline(
        positive: VoidCallback,
        negative: VoidCallback,
        retry: VoidCallback
    ) {
        positive()
    }

    override fun attestationCompletedWithWarnings(positive: VoidCallback) {
        positive()
    }

    override fun attestationDidFail(
        isDevCard: Boolean,
        positive: VoidCallback,
        negative: VoidCallback
    ) {
        positive()
    }

    override fun dismiss() {
        _viewVisibility.value = false
        listener.onTangemDelegateDismiss()
    }

    override fun onDelay(
        total: Int,
        current: Int,
        step: Int,
        productType: ProductType
    ) {
        // No custom handling is required for this callback in the current product flow.
    }

    override fun onError(error: TangemError) {
        listener.onError(error)
    }

    override fun onSecurityDelay(
        ms: Int,
        totalDurationSeconds: Int,
        productType: ProductType
    ) {
        listener.onSecurityDelay(ms, totalDurationSeconds)
    }

    override suspend fun onSessionStarted(
        cardId: String?,
        message: ViewDelegateMessage?,
        enableHowTo: Boolean,
        iconScanRes: Int?,
        productType: ProductType
    ) {

        _viewVisibility.value = true
        listener.onSessionStarted()
    }

    override fun onSessionStopped(
        message: Message?,
        onDialogHidden: () -> Unit
    ) {
        _viewVisibility.value = false
        listener.onSessionStopped()
        onDialogHidden()
    }

    override fun onTagConnected() {
        listener.onTagConnected()
    }

    override fun onTagLost(productType: ProductType) {
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
        listener.onUnsupportedCardFlow(
            unsupportedFlow = UnsupportedTangemFlow.USER_CODE_REQUESTED,
            onComplete = {
                callback(
                    CompletionResult.Failure(TangemSdkError.UserCancelled())
                )
            }
        )
    }

    override fun requestUserCodeChange(
        type: UserCodeType,
        cardId: String?,
        callback: CompletionCallback<String>
    ) {
        // User code change flow is intentionally not supported in this application.
        listener.onUnsupportedCardFlow(
            unsupportedFlow = UnsupportedTangemFlow.USER_CODE_CHANGE_REQUESTED,
            onComplete = {
                callback(
                    CompletionResult.Failure(TangemSdkError.UserCancelled())
                )
            }
        )
    }

    override fun setConfig(config: Config) {
        sdkConfig = config
    }

    override fun setMessage(message: ViewDelegateMessage?) {
        // Intentionally ignored to keep the existing custom UX unchanged.
    }

    override fun showWelcomeBackWarning(callback: CompletionCallback<Unit>) {
        listener.onWelcomeBackWarning(
            onContinue = {
                callback(CompletionResult.Success(Unit))
            },
            onReject = {
                callback(
                    CompletionResult.Failure(TangemSdkError.UserCancelled())
                )
            }
        )
    }

    interface CustomCardManagerDelegateListener {
        fun onSessionStarted()
        fun onSessionStopped()
        fun onTagConnected()
        fun onTagLost()
        fun onLostCard()
        fun onWrongCard()
        fun onSecurityDelay(ms: Int, totalDurationSeconds: Int)

        fun onError(error: TangemError)
        fun onTangemDelegateDismiss()

        fun onWelcomeBackWarning(
            onContinue: () -> Unit,
            onReject: () -> Unit
        )

        fun onUnsupportedCardFlow(
            unsupportedFlow: UnsupportedTangemFlow,
            onComplete: () -> Unit
        )
    }
}

enum class UnsupportedTangemFlow {
    USER_CODE_REQUESTED,
    USER_CODE_CHANGE_REQUESTED
}