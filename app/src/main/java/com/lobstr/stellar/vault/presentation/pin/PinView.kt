package com.lobstr.stellar.vault.presentation.pin

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

@Skip
interface PinView : MvpView {

    fun showPinFr(pinMode: Byte)

    fun finishApp()

    fun finishScreen()
}