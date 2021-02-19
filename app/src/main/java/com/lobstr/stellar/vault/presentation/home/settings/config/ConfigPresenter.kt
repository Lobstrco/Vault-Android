package com.lobstr.stellar.vault.presentation.home.settings.config

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.config.ConfigInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Auth
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.config.Config
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Code.Config.SPAM_PROTECTION
import com.lobstr.stellar.vault.presentation.util.Constant.Code.Config.TRANSACTION_CONFIRMATIONS
import com.lobstr.stellar.vault.presentation.util.Constant.Util.UNDEFINED_VALUE
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ConfigPresenter @Inject constructor(
    private val interactor: ConfigInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<ConfigView>() {

    var config: Int = UNDEFINED_VALUE

    private var updateAccountConfigInProcess = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        when (config) {
            UNDEFINED_VALUE -> viewState.finishScreen()
            else -> {
                viewState.setupToolbarTitle(
                    when (config) {
                        SPAM_PROTECTION -> AppUtil.getString(R.string.title_toolbar_spam_protection)
                        TRANSACTION_CONFIRMATIONS -> AppUtil.getString(R.string.text_settings_transaction_confirmation)
                        else -> null
                    }
                )

                viewState.setupConfigTitle(
                    when (config) {
                        SPAM_PROTECTION -> AppUtil.getString(R.string.text_settings_spam_protection)
                        TRANSACTION_CONFIRMATIONS -> AppUtil.getString(R.string.text_config_confirm_transactions_title)
                        else -> null
                    }
                )

                viewState.setupConfigDescription(
                    when (config) {
                        SPAM_PROTECTION -> AppUtil.getString(R.string.text_config_spam_protection_description)
                        TRANSACTION_CONFIRMATIONS -> AppUtil.getString(R.string.text_config_confirm_transactions_description)
                        else -> null
                    }
                )

                viewState.initListComponents(
                    listOf(
                        Config(
                            Constant.ConfigType.YES,
                            AppUtil.getConfigText(Constant.ConfigType.YES)
                        ),
                        Config(
                            Constant.ConfigType.NO,
                            AppUtil.getConfigText(Constant.ConfigType.NO)
                        )
                    ),
                    when (config) {
                        SPAM_PROTECTION -> AppUtil.getConfigType(!interactor.isSpamProtectionEnabled())
                        TRANSACTION_CONFIRMATIONS -> AppUtil.getConfigType(interactor.isTrConfirmationEnabled())
                        else -> -1
                    }
                )
            }
        }
    }

    fun configItemClicked(config: Config, selectedType: Byte) {
        // Prevent type selection duplications.
        if (config.type == selectedType) {
            return
        }

        when (this.config) {
            SPAM_PROTECTION -> updateAccountConfig(!AppUtil.getConfigValue(config.type))
            TRANSACTION_CONFIRMATIONS -> {
                interactor.setTrConfirmationEnabled(AppUtil.getConfigValue(config.type))
                viewState.setSelectedType(AppUtil.getConfigType(interactor.isTrConfirmationEnabled()))
            }
        }
    }

    private fun updateAccountConfig(spamProtectionEnabled: Boolean) {
        if (updateAccountConfigInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.updatedAccountConfig(spamProtectionEnabled)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    updateAccountConfigInProcess = true
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    updateAccountConfigInProcess = false
                }
                .subscribe({
                    interactor.setSpamProtectionEnabled(it.spamProtectionEnabled)
                    viewState.setSelectedType(AppUtil.getConfigType(!it.spamProtectionEnabled))
                }, {
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> updateAccountConfig(spamProtectionEnabled)
                            }
                        }
                        is DefaultException -> {
                            viewState.showErrorMessage(it.details)
                        }
                        else -> {
                            viewState.showErrorMessage(it.message ?: "")
                        }
                    }
                })
        )
    }

    fun infoClicked() {
        viewState.showHelpScreen(
            when (config) {
                TRANSACTION_CONFIRMATIONS -> Constant.Support.TRANSACTION_CONFIRMATIONS
                else -> -1
            },
            interactor.getUserPublicKey()
        )
    }
}