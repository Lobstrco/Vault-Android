package com.lobstr.stellar.vault.presentation.home.rate_us.suggest

import com.lobstr.stellar.vault.domain.suggest_rate_us.SuggestRateUsInteractor
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import moxy.MvpPresenter
import javax.inject.Inject

class SuggestRateUsPresenter @Inject constructor(private val interactor: SuggestRateUsInteractor) :
    MvpPresenter<SuggestRateUsView>() {

    fun rateClicked() {
        viewState.showRateUsDialog()
    }

    fun cancelClicked() {
        viewState.sendMail(
            Constant.Social.SUPPORT_MAIL,
            SupportManager.createSupportMailSubject(),
            SupportManager.createSupportMailBody(userId = interactor.getUserPublicKey())
        )
    }
}