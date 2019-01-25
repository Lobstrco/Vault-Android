package com.lobstr.stellar.vault.presentation.home.transactions.import_xdr

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface ImportXdrView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun showMessage(message: String?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun dismissProgressDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun successConfirmTransaction(envelopeXdr: String, needAdditionalSignatures: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun errorConfirmTransaction(errorMessage: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setSubmitEnabled(enabled: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFormError(show: Boolean, error: String?)
}