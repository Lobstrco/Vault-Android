package com.lobstr.stellar.vault.presentation.home.rate_us.feedback

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip


@Skip
interface ProvideFeedbackView : MvpView {
    fun sendMail(mail: String, subject: String, body: String? = null)
}