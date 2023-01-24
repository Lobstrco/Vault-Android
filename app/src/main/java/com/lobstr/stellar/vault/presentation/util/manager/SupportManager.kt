package com.lobstr.stellar.vault.presentation.util.manager

import android.content.Context
import android.os.Build
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.AppUtil

/**
 * Use for Help center actions.
 */
object SupportManager {

    object FreshdeskInfo {
        private const val URL = "https://lobstrvault.freshdesk.com/support/"
        private const val SOLUTIONS = URL.plus("solutions/")

        const val HELP_CENTER = URL.plus("home")
        const val ARTICLES = SOLUTIONS.plus("articles/")
    }

    object ArticleID {
        const val RECOVERY_PHRASE = 151000001282L
        const val RECOVER_ACCOUNT = 151000001581L
        const val SIGNING_WITH_VAULT_SIGNER_CARD = 151000001342L
        const val TRANSACTION_CONFIRMATIONS = 151000001333L
        const val SPAM_PROTECTION = 151000001285L
    }

    fun showFreshdeskArticle(context: Context, articleId: Long) {
        AppUtil.openWebPage(context, FreshdeskInfo.ARTICLES.plus(articleId))
    }

    fun showFreshdeskHelpCenter(context: Context) {
        AppUtil.openWebPage(context, FreshdeskInfo.HELP_CENTER)
    }

    fun createSupportMailSubject(context: Context = AppUtil.getAppContext()) = AppUtil.getString(
        R.string.text_mail_support_subject, BuildConfig.VERSION_NAME, AppUtil.getAppVersionCode(context)
    )

    fun createSupportMailBody(
        context: Context = AppUtil.getAppContext(),
        userId: String? = "not provided"
    ) = AppUtil.getString(R.string.text_mail_support_body,
        BuildConfig.VERSION_NAME, AppUtil.getAppVersionCode(context),
        userId ?: "not provided",
        Build.BRAND,
        Build.DEVICE,
        Build.MODEL,
        Build.VERSION.RELEASE
    )
}