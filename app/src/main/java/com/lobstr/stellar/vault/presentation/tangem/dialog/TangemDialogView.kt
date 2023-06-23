package com.lobstr.stellar.vault.presentation.tangem.dialog

import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.util.VibrateType
import com.tangem.common.card.EllipticCurve
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface TangemDialogView : MvpView {

    @Skip
    fun showNfcCheckDialog()

    @Skip
    fun showNfcDeviceSettings()

    @Skip
    fun showMessage(message: String?)

    @Skip
    fun successfullyCompleteOperation(tangemInfo: TangemInfo?)

    @Skip
    fun cancelOperation()

    @Skip
    fun finishScreen()

    @Skip
    fun startScanTangemCard()

    @AddToEndSingle
    fun setStateTitle(state: String?)

    @AddToEndSingle
    fun setDescriptionMessage(message: String?)

    @AddToEndSingle
    fun setTimerDescription(message: String?)

    @Skip
    fun startSignPendingTransaction(arrayHashes: Array<ByteArray>, walletPublicKey: ByteArray, cardId: String)

    @Skip
    fun startCreateWallet(curve: EllipticCurve, cardId: String)

    @AddToEndSingle
    fun setGravity(gravity: Int)

    @AddToEndSingle
    fun changeActionContainerVisibility(show: Boolean)

    @AddToEndSingle
    fun changeErrorContainerVisibility(show: Boolean)

    @AddToEndSingle
    fun setErrorContainerData(errorTitle: String, errorDescription: String)

    @AddToEndSingle
    fun showSuccessAnimation()

    @Skip
    fun vibrate(type: VibrateType)
}