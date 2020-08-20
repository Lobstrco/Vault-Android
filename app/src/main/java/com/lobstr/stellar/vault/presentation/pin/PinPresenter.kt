package com.lobstr.stellar.vault.presentation.pin

import com.lobstr.stellar.vault.presentation.util.Constant
import moxy.MvpPresenter

class PinPresenter(private var pinMode: Byte) : MvpPresenter<PinView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showPinFr(pinMode)
    }

    fun onBackPressed() {
        when (pinMode) {
            Constant.PinMode.ENTER -> viewState.finishApp()
            else -> viewState.finishScreen()
        }
    }
}