package com.lobstr.stellar.vault.presentation.base.activity

import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface BaseActivityView : MvpView {

    @Skip
    fun setActionBarTitle(title: String?)

    @Skip
    fun setActionBarTitle(@StringRes title: Int)

    @Skip
    fun setActionBarTitle(@StringRes title: Int, where: TextUtils.TruncateAt)

    @Skip
    fun setActionBarTitle(title: String, where: TextUtils.TruncateAt)

    @Skip
    fun setActionBarTitle(title: String, @ColorRes color: Int)

    @Skip
    fun setActionBarTitleColor(@ColorRes color: Int)

    @Skip
    fun setActionBarIcon(iconRes: Int)

    @AddToEndSingle
    fun changeActionBarIconVisibility(visible: Boolean)

    @Skip
    fun setActionBarBackground(@DrawableRes background: Int)

    @Skip
    fun setHomeAsUpIndicator(@DrawableRes image: Int, @ColorRes color: Int)

    @Skip
    fun showTangemScreen(tangemInfo: TangemInfo)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showMessage(message: String?)

    @Skip
    fun proceedPinActivityAppearance()
}