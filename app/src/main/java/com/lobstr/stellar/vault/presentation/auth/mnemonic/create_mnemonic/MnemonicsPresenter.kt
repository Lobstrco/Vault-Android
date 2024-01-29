package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import com.lobstr.stellar.vault.domain.mnemonics.MnemonicsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager.ArticleID.RECOVERY_PHRASE
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MnemonicsPresenter @Inject constructor(val interactor: MnemonicsInteractor) : BasePresenter<MnemonicsView>() {

    var generate: Boolean = false

    private var mnemonicItemList: ArrayList<MnemonicItem>? = null

    private var mnemonicsStr: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (generate) {
            setupMnemonics(interactor.generate12WordMnemonics())
        } else {
            viewState.setActionLayerVisibility(false)
            getExistingMnemonics()
        }
    }

    private fun getExistingMnemonics() {
        unsubscribeOnDestroy(
            interactor.getExistingMnemonics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setupMnemonics(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun setupMnemonics(mnemonicItems: ArrayList<MnemonicItem>) {
        mnemonicsStr = mnemonicItems.joinToString(" ") { it.value }

        mnemonicItemList = mnemonicItems
        viewState.setupMnemonics(mnemonicItems)
    }

    fun infoClicked() {
        viewState.showHelpScreen(RECOVERY_PHRASE)
    }

    fun nextClicked() {
        viewState.showConfirmationScreen(mnemonicItemList!!)
    }

    fun clipToBordClicked() {
        if (mnemonicsStr.isNullOrEmpty()) {
            return
        }

        viewState.copyToClipBoard(mnemonicsStr!!)
    }

    fun backPressed() {
        viewState.showDenyAccountCreationDialog()
    }

    fun screenCaptured() {
        viewState.showScreenCaptureWarning()
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.DENY_ACCOUNT_CREATION -> viewState.showAuthFr()
        }
    }
}