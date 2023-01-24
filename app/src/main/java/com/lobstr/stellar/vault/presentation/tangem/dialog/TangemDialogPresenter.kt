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
import com.lobstr.stellar.vault.presentation.util.VibratorUtil.VibrateType.TYPE_TWO
import com.tangem.TangemSdkError
import com.tangem.commands.Card
import com.tangem.commands.CreateWalletResponse
import com.tangem.commands.SignResponse
import com.tangem.tangem_sdk_new.ui.NfcLocation
import javax.inject.Inject

class TangemDialogPresenter @Inject constructor(private val interactor: TangemInteractor) :
    BasePresenter<TangemDialogView>() {

    var tangemInfo: TangemInfo? = null

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
                startCreateWallet()
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
        viewState.setStateTitle(AppUtil.getString(R.string.text_tv_tangem_dialog_tittle__scan))
        viewState.setDescriptionMessage(AppUtil.getString(R.string.text_tv_tangem_dialog_description_scan))
        viewState.startScanTangemCard()
    }

    private fun startCreateWallet() {
        mAction = Constant.TangemAction.ACTION_CREATE_WALLET
        viewState.setStateTitle(AppUtil.getString(R.string.text_tv_tangem_dialog_tittle__scan))
        viewState.setDescriptionMessage(AppUtil.getString(R.string.text_tv_tangem_dialog_description_scan))
        viewState.startCreateWallet(tangemInfo?.cardId!!)
    }

    private fun startSignTransaction() {
        mAction = Constant.TangemAction.ACTION_SIGN
        viewState.setStateTitle(AppUtil.getString(R.string.text_tv_tangem_dialog_tittle__scan))
        viewState.setDescriptionMessage(AppUtil.getString(R.string.text_tv_tangem_dialog_description_scan))
        if (!tangemInfo?.message.isNullOrEmpty()) {
            viewState.setStateTitle(tangemInfo?.message)
        }
        if (!tangemInfo?.description.isNullOrEmpty()) {
            viewState.setDescriptionMessage(tangemInfo?.description)
        }
        prepareSignTransaction()
    }

    private fun setNfcLocation() {
        val codename = Build.DEVICE
        var locationHeight = 50

        for (nfcLocation in NfcLocation.values()) {
            if (codename.startsWith(nfcLocation.codename)) {
                locationHeight = nfcLocation.y
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
        tangemInfo?.cardStatus = data.status?.name
        tangemInfo?.cardStatusCode = data.status?.code

        if(tangemInfo?.cardStatusCode == Constant.TangemCardStatus.LOADED){
            tangemInfo?.accountId = interactor.getPublicKeyFromKeyPair(data.walletPublicKey)
        }

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
        viewState.startSignPendingTransaction(arrayHashesPendingTransaction, tangemInfo?.cardId!!)
    }

    fun processSignedData(signResponse: SignResponse) {
        val pendingTransaction = interactor.getTransactionFromXDR(tangemInfo?.pendingTransaction!!)
        val signedTransactionXDR: String? = interactor.signTransactionWithTangemCardData(
            pendingTransaction,
            signResponse,
            tangemInfo?.accountId!!
        )
        tangemInfo?.signedTransaction = signedTransactionXDR

        viewState.showSuccessAnimation()
    }

    fun processDataAfterCreateWallet(data: CreateWalletResponse) {
        if (tangemInfo == null) {
            tangemInfo = TangemInfo()
        }
        tangemInfo?.cardId = data.cardId
        tangemInfo?.cardStatus = data.status.name
        tangemInfo?.cardStatusCode = data.status.code

        if(tangemInfo?.cardStatusCode == Constant.TangemCardStatus.LOADED){
            tangemInfo?.accountId = interactor.getPublicKeyFromKeyPair(data.walletPublicKey)
        }
        viewState.showSuccessAnimation()
    }

    private fun successfullyCompleteScreen() {
        viewState.successfullyCompleteOperation(tangemInfo)
    }

    fun processErrorCompletion(error: TangemSdkError) {
        val tangemError = interactor.handleTangemError(error)
        when (tangemError?.errorMod) {
            Constant.TangemErrorMod.ERROR_MOD_FINISH_SCREEN -> {
                viewState.showMessage(
                    tangemError.errorTitle
                        ?: AppUtil.getString(R.string.text_tv_tangem_default_error_header)
                )
            }
            Constant.TangemErrorMod.ERROR_MOD_ONLY_TEXT -> {
                viewState.showMessage(
                    tangemError.errorTitle
                        ?: AppUtil.getString(R.string.text_tv_tangem_default_error_header)
                )
            }
            Constant.TangemErrorMod.ERROR_MOD_DEFAULT -> {
                viewState.showMessage(
                    tangemError.errorTitle
                        ?: AppUtil.getString(R.string.text_tv_tangem_default_error_header)
                )
            }
            Constant.TangemErrorMod.ERROR_MOD_REPEAT_ACTION -> {
                showErrorWithRepeatAction(tangemError.errorTitle, tangemError.errorDescription)
            }
            else -> {
                viewState.showMessage(
                    tangemError?.errorTitle
                        ?: AppUtil.getString(R.string.text_tv_tangem_default_error_header)
                )
            }
        }
    }

    private fun showErrorWithRepeatAction(errorTitle: String?, errorDescription: String?) {
        viewState.changeActionContainerVisibility(false)
        viewState.changeErrorContainerVisibility(true)
        viewState.setErrorContainerData(
            errorTitle
                ?: AppUtil.getString(R.string.text_tv_tangem_default_error_header),
            errorDescription
                ?: AppUtil.getString(R.string.text_tv_tangem_default_error_description)
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
                startCreateWallet()
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
}