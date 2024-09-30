package com.lobstr.stellar.vault.presentation.pin.main

import android.app.Activity
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface PinFrView : MvpView {

    @AddToEndSingle
    fun setupNavigationBar(light: Boolean)

    @AddToEndSingle
    fun setupToolbar(
        hasMenu: Boolean,
        @ColorRes toolbarColor: Int,
        @DrawableRes upArrow: Int,
        @ColorRes upArrowColor: Int
    )

    @AddToEndSingle
    fun showHomeAsUp(show: Boolean)

    @AddToEndSingle
    fun showTitle(@StringRes title: Int)

    @AddToEndSingle
    fun setScreenStyle(style: Int)

    @Skip
    fun resetPin()

    @AddToEndSingle
    fun showHomeScreen()

    @AddToEndSingle
    fun showVaultAuthScreen()

    @Skip
    fun showBiometricSetUpScreen(pinMode: Byte)

    @Skip
    fun showErrorMessage(@StringRes message: Int)

    @AddToEndSingle
    fun setResult(resultCode: Int)

    @AddToEndSingle
    fun finishScreen(resultCode: Int = Activity.RESULT_CANCELED)

    @Skip
    fun finishApp()

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showBiometricDialog()

    @AddToEndSingle
    fun showAuthScreen()

    @Skip
    fun showLogOutDialog()

    @Skip
    fun showCommonPinPatternDialog()

    @Skip
    fun sendMail(mail: String, subject: String, body: String? = null)
}