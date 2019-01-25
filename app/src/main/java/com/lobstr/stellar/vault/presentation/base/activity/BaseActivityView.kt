package com.lobstr.stellar.vault.presentation.base.activity

import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface BaseActivityView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarTitle(title: String)

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarTitle(@StringRes title: Int)

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarTitle(@StringRes title: Int, where: TextUtils.TruncateAt)

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarTitle(title: String, where: TextUtils.TruncateAt)

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarTitle(title: String, @ColorRes color: Int)

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarTitleColor(@ColorRes color: Int)

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarIcon(iconRes: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun changeActionBarIconVisibility(visible: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun setActionBarBackground(@DrawableRes background: Int)

    @StateStrategyType(SkipStrategy::class)
    fun setHomeAsUpIndicator(@DrawableRes image: Int, @ColorRes color: Int)
}