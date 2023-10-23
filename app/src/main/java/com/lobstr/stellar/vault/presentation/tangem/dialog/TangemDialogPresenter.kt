package com.lobstr.stellar.vault.presentation.tangem.dialog

import android.os.Build
import android.view.Gravity
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.tangem.TangemInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.VibrateType.*
import com.tangem.common.card.Card
import com.tangem.common.card.EllipticCurve
import com.tangem.operations.sign.SignResponse
import com.tangem.operations.wallet.CreateWalletResponse
import com.tangem.tangem_sdk_new.ui.NfcLocation
import javax.inject.Inject

class TangemDialogPresenter @Inject constructor(private val interactor: TangemInteractor) :
    BasePresenter<TangemDialogView>() {

    var tangemInfo: TangemInfo? = null
    var tangemOperationType: Int = NO_ACTION

    private var mAction = Constant.TangemAction.ACTION_DEFAULT

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (tangemInfo == null) {
            tangemInfo = TangemInfo()
        }
        setNfcLocation()
    }

    override fun attachView(view: TangemDialogView?) {
        super.attachView(view)
        when {
            tangemInfo?.cardId.isNullOrEmpty() -> {
                startScanTangemCard()
            }
            !tangemInfo?.cardId.isNullOrEmpty() && tangemInfo?.accountId.isNullOrEmpty() -> {
                if (tangemInfo?.curve != null) {
                    startCreateWallet(tangemInfo?.curve!!)
                }
            }
            !tangemInfo?.cardId.isNullOrEmpty() && !tangemInfo?.accountId.isNullOrEmpty() &&
                    !tangemInfo?.pendingTransaction.isNullOrEmpty() -> {
                startSignTransaction()
            }
            else -> {
                viewState.finishScreen()
            }
        }
    }

    private fun startScanTangemCard() {
        mAction = Constant.TangemAction.ACTION_SCAN
        setStateDescription(TangemDialogStateType.START_SCAN)
        viewState.startScanTangemCard()
    }

    private fun startCreateWallet(curve: EllipticCurve) {
        mAction = Constant.TangemAction.ACTION_CREATE_WALLET
        setStateDescription(TangemDialogStateType.START_CREATE_WALLET)
        viewState.startCreateWallet(curve, tangemInfo?.cardId!!)
    }

    private fun startSignTransaction() {
        mAction = Constant.TangemAction.ACTION_SIGN
        viewState.setStateTitle(AppUtil.getString(R.string.tangem_view_scan_title))
        viewState.setDescriptionMessage(AppUtil.getString(R.string.tangem_view_scan_description))
        if (!tangemInfo?.message.isNullOrEmpty()) {
            viewState.setStateTitle(tangemInfo?.message)
        }
        if (!tangemInfo?.description.isNullOrEmpty()) {
            viewState.setDescriptionMessage(tangemInfo?.description)
        }
        prepareSignTransaction()
        viewState.vibrate(TYPE_ONE)
    }

    private fun setNfcLocation() {
        val codename = Build.DEVICE
        var locationHeight = 50

        for (nfcLocation in NfcLocation.entries) {
            if (codename.startsWith(nfcLocation.codename)) {
                locationHeight = nfcLocation.y.toInt()
            }
        }

        val gravity = when (locationHeight) {
            in 1..29 -> {
                Gravity.TOP
            }
            in 30..70 -> {
                Gravity.CENTER_VERTICAL
            }
            in 71..99 -> {
                Gravity.BOTTOM
            }
            else -> {
                Gravity.CENTER_VERTICAL
            }
        }

        viewState.setGravity(gravity)
    }

    fun processDataAfterScanningCard(data: Card) {
        if (tangemInfo == null) {
            tangemInfo = TangemInfo()
        }

        tangemInfo?.cardId = data.cardId

        tangemInfo?.cardStatusCode = when {
            data.wallets.isEmpty() -> {
                Constant.TangemCardStatus.EMPTY
            }
            data.wallets.isNotEmpty() -> {
                Constant.TangemCardStatus.WALLET_CREATED
            }
            else -> {
                Constant.TangemCardStatus.NOT_PERSONALIZED
            }
        }

        if (tangemInfo?.cardStatusCode == Constant.TangemCardStatus.WALLET_CREATED) {
            tangemInfo?.accountId =
                interactor.getPublicKeyFromKeyPair(data.wallets.first().publicKey)
            tangemInfo?.curve = data.wallets.first().curve
        } else {
            tangemInfo?.curve = data.supportedCurves[0]
        }

        setStateDescription(TangemDialogStateType.SUCCESSFULLY_SCANNING)
        viewState.showSuccessAnimation()
    }

    private fun prepareSignTransaction() {
        if (tangemInfo?.cardId.isNullOrEmpty() || tangemInfo?.accountId.isNullOrEmpty()) {
            viewState.finishScreen()
            return
        }

        val pendingTransaction = interactor.getTransactionFromXDR(tangemInfo?.pendingTransaction!!)
        val hashPendingTransaction = pendingTransaction.hash()
        val arrayHashesPendingTransaction: Array<ByteArray> = arrayOf(hashPendingTransaction)

        mAction = Constant.TangemAction.ACTION_SIGN

        if (tangemInfo?.accountId == null || tangemInfo?.cardId == null) {
            return
        }

        val walletPublicKey = interactor.getPublicKeyFromKeyPair(tangemInfo?.accountId) ?: return

        viewState.startSignPendingTransaction(
            arrayHashesPendingTransaction,
            walletPublicKey,
            tangemInfo?.cardId!!
        )
    }

    fun processSignedData(signResponse: SignResponse) {
        val pendingTransaction = interactor.getTransactionFromXDR(tangemInfo?.pendingTransaction!!)
        val signedTransactionXDR: String? = interactor.signTransactionWithTangemCardData(
            pendingTransaction,
            signResponse,
            tangemInfo?.accountId!!
        )
        tangemInfo?.signedTransaction = signedTransactionXDR

        setStateDescription(TangemDialogStateType.SUCCESSFULLY_SIGNED)
        viewState.showSuccessAnimation()
    }

    fun processDataAfterCreateWallet(data: CreateWalletResponse) {
        if (tangemInfo == null) {
            tangemInfo = TangemInfo()
        }
        tangemInfo?.cardId = data.cardId
        tangemInfo?.cardStatusCode = Constant.TangemCardStatus.WALLET_CREATED
        tangemInfo?.curve = data.wallet.curve
        if (tangemInfo?.cardStatusCode == Constant.TangemCardStatus.WALLET_CREATED) {
            tangemInfo?.accountId =
                interactor.getPublicKeyFromKeyPair(data.wallet.publicKey)

        }

        viewState.showSuccessAnimation()
    }

    private fun successfullyCompleteScreen() {
        viewState.successfullyCompleteOperation(tangemInfo)
    }

    fun processErrorCompletion(error: com.tangem.common.core.TangemError) {
        val tangemError = interactor.handleTangemError(error)
        when (tangemError?.errorMod) {
            Constant.TangemErrorMod.ERROR_MOD_USER_CANCELLED -> {
                viewState.finishScreen()//todo 50002
            }
            Constant.TangemErrorMod.ERROR_MOD_FINISH_SCREEN -> {
                viewState.showMessage(
                    tangemError.errorTitle
                        ?: AppUtil.getString(R.string.tangem_error_default_title)
                )
            }
            Constant.TangemErrorMod.ERROR_MOD_ONLY_TEXT -> {
                viewState.showMessage(
                    tangemError.errorTitle
                        ?: AppUtil.getString(R.string.tangem_error_default_title)
                )
            }
            Constant.TangemErrorMod.ERROR_MOD_DEFAULT -> {
                viewState.showMessage(
                    tangemError.errorTitle
                        ?: AppUtil.getString(R.string.tangem_error_default_title)
                )
            }
            Constant.TangemErrorMod.ERROR_MOD_REPEAT_ACTION -> {
                showErrorWithRepeatAction(tangemError.errorTitle, tangemError.errorDescription)
            }
            else -> {
                viewState.showMessage(
                    tangemError?.errorTitle
                        ?: AppUtil.getString(R.string.tangem_error_default_title)
                )
            }
        }
    }

    private fun showErrorWithRepeatAction(errorTitle: String?, errorDescription: String?) {
        viewState.changeActionContainerVisibility(false)
        viewState.changeErrorContainerVisibility(true)
        viewState.setErrorContainerData(
            errorTitle
                ?: AppUtil.getString(R.string.tangem_error_default_title),
            errorDescription
                ?: AppUtil.getString(R.string.tangem_error_default_description)
        )
    }

    fun tryAgainClicked() {
        viewState.changeActionContainerVisibility(true)
        viewState.changeErrorContainerVisibility(false)
        repeatAction()
    }

    private fun repeatAction() {
        when (mAction) {
            Constant.TangemAction.ACTION_SCAN -> {
                startScanTangemCard()
            }
            Constant.TangemAction.ACTION_SIGN -> {
                startSignTransaction()
            }
            Constant.TangemAction.ACTION_CREATE_WALLET -> {
                if (tangemInfo?.curve != null) {
                    startCreateWallet(tangemInfo?.curve!!)
                }
            }
            else -> {
                viewState.finishScreen()
            }
        }
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.NFC_INFO_DIALOG -> {
                viewState.showNfcDeviceSettings()
            }
        }
    }

    fun interruptionOfSignatureOperation() {
        //viewState.showMessage("Lost Card")
    }

    fun cancelClicked() {
        viewState.finishScreen()
    }

    fun successAnimationFinished() {
        successfullyCompleteScreen()
    }

    fun successAnimationStarted() {
        viewState.vibrate(TYPE_TWO)
    }

    //====================

    companion object TangemOperation {
        const val NO_ACTION = -1
        const val SCANNING = 0
        const val CREATE_WALLET = 1
        const val SIGN = 2
        const val SIGN_FOR_SIGN_IN = 3
    }

    private object TangemDialogStateType {
        const val SUCCESSFULLY_SIGNED = 3
        const val SECURITY_DELAY = 4
        const val TAG_CONNECTED = 5
        const val TAG_LOST = 6
        const val START_SCAN = 7
        const val START_CREATE_WALLET = 8
        const val SUCCESSFULLY_SCANNING = 9
    }

    fun onTagSessionStoppedTangem() {
        // Add logic if needed.
    }

    fun onTagConnectedTangem() {
        setStateDescription(TangemDialogStateType.TAG_CONNECTED)
    }

    fun onTagLostsTangem() {
        setStateDescription(TangemDialogStateType.TAG_LOST)
    }

    fun onSecurityDelayTangem(ms: Int, totalDurationSeconds: Int) {
        setStateDescription(TangemDialogStateType.SECURITY_DELAY, ms / 100)
    }

    private fun setStateDescription(state: Int, securityDelay: Int? = null) {
        var title = ""
        var description = ""
        var delay = ""
        var vibrate = false

        when (state) {
            TangemDialogStateType.START_SCAN -> {
                title = AppUtil.getString(R.string.tangem_view_scan_title)
                description =
                    AppUtil.getString(R.string.tangem_view_scan_description)
                vibrate = true
            }
            TangemDialogStateType.TAG_CONNECTED -> {
                title = AppUtil.getString(R.string.tangem_view_tag_connected_title)
                description =
                    if (mAction == Constant.TangemAction.ACTION_SIGN && tangemOperationType != SIGN_FOR_SIGN_IN)
                        AppUtil.getString(R.string.tangem_view_tag_connected_for_sign_tr_description) else
                        AppUtil.getString(R.string.tangem_view_tag_connected_description)
                delay = ""
                vibrate = true
            }
            TangemDialogStateType.TAG_LOST -> {
                title = AppUtil.getString(R.string.tangem_view_tag_lost_title)
                description = AppUtil.getString(R.string.tangem_view_tag_lost_description)
                delay = ""
                vibrate = true
            }
            TangemDialogStateType.SUCCESSFULLY_SCANNING -> {
                title =
                    AppUtil.getString(R.string.tangem_view_successfully_scanning_title)
                description =
                    AppUtil.getString(R.string.tangem_view_successfully_scanning_description)
                delay = ""
                vibrate = false
                tangemOperationType = NO_ACTION
            }
            TangemDialogStateType.SUCCESSFULLY_SIGNED -> {
                title =
                    if (tangemOperationType == SIGN_FOR_SIGN_IN)
                        AppUtil.getString(R.string.tangem_view_successfully_scanning_title) else
                        AppUtil.getString(R.string.tangem_view_successfully_signed_title)
                description =
                    if (tangemOperationType == SIGN_FOR_SIGN_IN)
                        AppUtil.getString(R.string.tangem_view_successfully_signed_for_login_description) else
                        AppUtil.getString(R.string.tangem_view_successfully_signed_description)
                delay = ""
                vibrate = false
                tangemOperationType = NO_ACTION
            }
            TangemDialogStateType.SECURITY_DELAY -> {
                title = AppUtil.getString(R.string.tangem_view_tag_connected_title)
                description =
                    if (mAction == Constant.TangemAction.ACTION_SIGN && tangemOperationType != SIGN_FOR_SIGN_IN)
                        AppUtil.getString(R.string.tangem_view_tag_connected_for_sign_tr_description) else
                        AppUtil.getString(R.string.tangem_view_tag_connected_description)
                delay = securityDelay?.toString() ?: ""
                vibrate = true
            }
            TangemDialogStateType.START_CREATE_WALLET -> {
                title = AppUtil.getString(R.string.tangem_view_scan_title)
                description =
                    AppUtil.getString(R.string.tangem_view_scan_description)
                vibrate = true
            }
        }

        viewState.setStateTitle(title)
        viewState.setDescriptionMessage(description)
        viewState.setTimerDescription(delay)
        if (vibrate) {
            viewState.vibrate(TYPE_FOUR)
        }
    }

    fun onWrongCard() {
        viewState.showMessage(
            AppUtil.getString(R.string.tangem_error_wrong_card_text)
        )
    }
}