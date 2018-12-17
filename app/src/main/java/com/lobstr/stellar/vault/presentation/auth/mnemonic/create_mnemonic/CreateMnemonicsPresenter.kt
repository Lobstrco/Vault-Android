package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.soneso.stellarmnemonics.Wallet

@InjectViewState
class CreateMnemonicsPresenter : MvpPresenter<CreateMnemonicsView>() {

    private lateinit var mnemonicsArray: CharArray

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        generateMnemonics()
    }

    private fun generateMnemonics() {
        mnemonicsArray = Wallet.generate24WordMnemonic()
        val mnemonicsStr = String(mnemonicsArray)
        viewState.setupMnemonics(mnemonicsStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() })
    }

    fun proceedClicked() {
        viewState.showConfirmationScreen(mnemonicsArray)
    }
}