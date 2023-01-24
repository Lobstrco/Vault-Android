package com.lobstr.stellar.vault.presentation.home.settings.config

import com.lobstr.stellar.vault.presentation.entities.config.Config
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ConfigView : MvpView {

    @AddToEndSingle
    fun setupToolbarTitle(title: String?)

    @AddToEndSingle
    fun setupConfigTitle(title: String?)

    @AddToEndSingle
    fun initListComponents(configs: List<Config>, selectedType: Byte)

    @AddToEndSingle
    fun setSelectedType(selectedType: Byte, config: Int)

    @AddToEndSingle
    fun setupConfigDescription(description: String?)

    @Skip
    fun finishScreen()

    @AddToEndSingle
    fun showProgressDialog(show: Boolean)

    @Skip
    fun showErrorMessage(message: String)

    @Skip
    fun showHelpScreen(articleId: Long)
}