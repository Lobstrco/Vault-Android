package com.lobstr.stellar.vault.presentation.util.manager

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.AppUtil
import zendesk.core.AnonymousIdentity
import zendesk.core.Zendesk
import zendesk.support.guide.HelpCenterActivity
import zendesk.support.guide.ViewArticleActivity
import zendesk.support.request.RequestActivity

/**
 * Use for Help center actions.
 */
object SupportManager {

    /**
     * Required setup.
     */
    private fun setZendeskIdentity(name: String? = null, email: String? = null) {
        Zendesk.INSTANCE.setIdentity(
            AnonymousIdentity.Builder()
                .apply {
                    name?.let {
                        withNameIdentifier(name)
                    }
                    email?.let {
                        withEmailIdentifier(email)
                    }
                }
                .build()
        )
    }

    fun showZendeskArticle(
        context: Context,
        articleId: Long,
        userId: String? = "not provided",
        contactUsVisible: Boolean = false,
        requestSubject: String? = if (contactUsVisible) AppUtil.getString(R.string.zendesk_request_subject) else null,
        tags: List<String>? = if (contactUsVisible) listOf(
            createZendeskDeviceInfoTag(context),
            createZendeskUserInfoTag(userId = userId)
        ) else null
    ) {
        setZendeskIdentity(name = userId ?: "not provided")
        val builder = ViewArticleActivity.builder(articleId)
            .withContactUsButtonVisible(contactUsVisible)
        ContextCompat.startActivity(context, if (contactUsVisible) {
            builder.intent(
                context, RequestActivity.builder()
                    .apply {
                        requestSubject?.let { withRequestSubject(it) }
                        tags?.let { withTags(it) }
                    }
                    .config()
            )
        } else {
            builder.intent(context)
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        },
            null
        )
    }

    fun showZendeskRequest(
        context: Context,
        userId: String? = "not provided",
        requestSubject: String? = AppUtil.getString(R.string.zendesk_request_subject),
        tags: List<String>? = listOf(
            createZendeskDeviceInfoTag(context),
            createZendeskUserInfoTag(userId = userId)
        )
    ) {
        setZendeskIdentity(name = userId ?: "not provided")
        ContextCompat.startActivity(context, RequestActivity.builder()
            .apply {
                requestSubject?.let { withRequestSubject(it) }
                tags?.let { withTags(it) }
            }
            .intent(context).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            },
            null
        )
    }

    fun showZendeskHelpCenter(
        context: Context,
        categoryIds: List<Long>? = null,
        sectionIds: List<Long>? = null,
        userId: String? = "not provided",
        showConversationsMenuButton: Boolean = false,
        contactUsVisibleInHelpCenter: Boolean = false,
        contactUsVisibleInArticle: Boolean = false,
        requestSubject: String? = if (contactUsVisibleInHelpCenter || contactUsVisibleInArticle) AppUtil.getString(
            R.string.zendesk_request_subject
        ) else null,
        tags: List<String>? = if (contactUsVisibleInHelpCenter || contactUsVisibleInArticle) listOf(
            createZendeskDeviceInfoTag(context),
            createZendeskUserInfoTag(userId = userId)
        ) else null
    ) {
        setZendeskIdentity(name = userId ?: "not provided")
        ContextCompat.startActivity(context, HelpCenterActivity
            .builder()
            .withShowConversationsMenuButton(showConversationsMenuButton)
            .withContactUsButtonVisible(contactUsVisibleInHelpCenter)
            .apply {
                categoryIds?.let {
                    withArticlesForCategoryIds(categoryIds)
                }

                sectionIds?.let {
                    withArticlesForSectionIds(sectionIds)
                }
            }
            .intent(
                context,
                ViewArticleActivity.builder()
                    .withContactUsButtonVisible(contactUsVisibleInArticle)
                    .config(),
                if (contactUsVisibleInHelpCenter || contactUsVisibleInArticle) {
                    RequestActivity.builder()
                        .apply {
                            requestSubject?.let { withRequestSubject(it) }
                            tags?.let { withTags(it) }
                        }
                        .config()
                } else {
                    null
                })
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            },
            null
        )
    }

    private fun createZendeskDeviceInfoTag(context: Context = AppUtil.getAppContext()) =
        String.format(
            AppUtil.getString(R.string.zd_info),
            Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
            AppUtil.getAppVersionCode(context),
            Build.DEVICE, Build.MODEL, Build.BRAND
        )

    private fun createZendeskUserInfoTag(userId: String? = "not provided") = String.format(
        AppUtil.getString(R.string.zd_user_id),
        userId ?: "not provided"
    )

    fun createSupportMailSubject(context: Context = AppUtil.getAppContext()) = String.format(
        AppUtil.getString(R.string.text_mail_support_subject),
        BuildConfig.VERSION_NAME, AppUtil.getAppVersionCode(context)
    )

    fun createSupportMailBody(
        context: Context = AppUtil.getAppContext(),
        userId: String? = "not provided"
    ) = String.format(
        AppUtil.getString(R.string.text_mail_support_body),
        BuildConfig.VERSION_NAME, AppUtil.getAppVersionCode(context),
        userId ?: "not provided",
        Build.BRAND,
        Build.DEVICE,
        Build.MODEL,
        Build.VERSION.RELEASE
    )
}