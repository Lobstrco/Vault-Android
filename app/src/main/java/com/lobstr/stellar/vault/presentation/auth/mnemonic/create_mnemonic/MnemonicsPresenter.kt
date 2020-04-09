package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import com.lobstr.stellar.vault.domain.mnemonics.MnemonicsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.mnemonics.MnemonicsModule
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MnemonicsPresenter(private val generate: Boolean) : BasePresenter<MnemonicsView>() {

    @Inject
    lateinit var interactor: MnemonicsInteractor

    init {
        LVApplication.appComponent.plusMnemonicsComponent(MnemonicsModule()).inject(this)
    }

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
        viewState.showHelpScreen()
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

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            AlertDialogFragment.DialogFragmentIdentifier.DENY_ACCOUNT_CREATION -> viewState.showAuthFr()
        }
    }
}