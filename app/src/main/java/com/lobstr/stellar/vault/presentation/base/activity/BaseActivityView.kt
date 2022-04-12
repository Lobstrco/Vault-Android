package com.lobstr.stellar.vault.presentation.base.activity

import android.graphics.drawable.Drawable
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
    fun setActionBarIcon(@DrawableRes icon: Int)

    @Skip
    fun setActionBarIcon(icon: Drawable?)

    @Skip
    fun setActionBarIcon(@DrawableRes icon: Int, @ColorRes color: Int)

    @AddToEndSingle
    fun changeActionBarIconVisibility(visible: Boolean)

    @Skip
    fun setActionBarBackground(@DrawableRes background: Int)

    @AddToEndSingle
    fun showTangemScreen(show: Boolean, tangemInfo: TangemInfo? = null)

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showMessage(message: String?)

    @Skip
    fun proceedPinActivityAppearance()

    @Skip
    fun checkAppVersionBehavior()

    @Skip
    fun showAppUpdateDialog(
        show: Boolean,
        title: String? = null,
        message: String? = null,
        positiveBtnText: String? = null,
        negativeBtnText: String? = null
    )

    @Skip
    fun showStore(storeUrl: String)

    @Skip
    fun reloadAccountData()
}