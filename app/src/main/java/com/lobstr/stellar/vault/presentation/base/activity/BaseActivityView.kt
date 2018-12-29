package com.lobstr.stellar.vault.presentation.base.activity

import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface BaseActivityView : MvpView {

    fun setActionBarTitle(title: String)

    fun setActionBarTitle(@StringRes title: Int)

    fun setActionBarTitle(@StringRes title: Int, where: TextUtils.TruncateAt)

    fun setActionBarTitle(title: String, where: TextUtils.TruncateAt)

    fun setActionBarTitle(title: String, @ColorRes color: Int)

    fun setActionBarTitleColor(@ColorRes color: Int)

    fun setActionBarIcon(iconRes: Int)

    fun changeActionBarIconVisibility(visible: Boolean)

    fun setActionBarBackground(@DrawableRes background: Int)

    fun setHomeAsUpIndicator(@DrawableRes image: Int, @ColorRes color: Int)
}