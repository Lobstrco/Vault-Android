package com.lobstr.stellar.vault.presentation.pin

import moxy.MvpPresenter

class PinPresenter(private var pinMode: Byte) : MvpPresenter<PinView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showPinFr(pinMode)
    }
}