package com.lobstr.stellar.vault.presentation.home.rate_us.feedback

import com.lobstr.stellar.vault.domain.suggest_rate_us.ProvideFeedbackInteractor
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import moxy.MvpPresenter
import javax.inject.Inject


class ProvideFeedbackPresenter @Inject constructor(private val interactor: ProvideFeedbackInteractor) :
    MvpPresenter<ProvideFeedbackView>() {

    fun contactUsClicked() {
        viewState.sendMail(
            Constant.Social.SUPPORT_MAIL,
            SupportManager.createSupportMailSubject(),
            SupportManager.createSupportMailBody(userId = interactor.getUserPublicKey())
        )
    }
}