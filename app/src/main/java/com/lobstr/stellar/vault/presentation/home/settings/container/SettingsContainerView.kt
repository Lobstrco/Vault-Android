package com.lobstr.stellar.vault.presentation.home.settings.container

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


@StateStrategyType(SkipStrategy::class)
interface SettingsContainerView : MvpView {

    fun showSettingsFr()

    fun showInfoFr()
}