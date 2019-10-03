package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.NoInternetConnectionException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.domain.signed_account.SignedAccountInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.signed_account.SignedAccountModule
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class SignedAccountsPresenter : BasePresenter<SignedAccountsView>() {

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    @Inject
    lateinit var mInteractor: SignedAccountInteractor

    // for restore RecycleView position after saveInstanceState (-1 - undefined state)
    private var savedRvPosition: Int = Constant.Util.UNDEFINED_VALUE

    init {
        LVApplication.appComponent.plusSignedAccountComponent(SignedAccountModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.title_toolbar_signed_accounts)
        viewState.initRecycledView()
        registerEventProvider()
        loadSignedAccountsList()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                loadSignedAccountsList()
                            }
                            needCheckConnectionState = false
                            cancelNetworkWorker()
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
        unsubscribeOnDestroy(
            eventProviderModule.notificationEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Notification.Type.REMOVED_SIGNER, Notification.Type.SIGNED_NEW_ACCOUNT -> loadSignedAccountsList()
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun loadSignedAccountsList() {
        unsubscribeOnDestroy(
            mInteractor.getSignedAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showProgress() }
                .doOnEvent { _, _ -> viewState.hideProgress() }
                .subscribe({
                    // reset saved scroll position for avoid scroll to wrong position after
                    // pagination action
                    savedRvPosition = Constant.Util.UNDEFINED_VALUE

                    if (it.isEmpty()) {
                        viewState.showEmptyState()
                    } else {
                        viewState.hideEmptyState()
                    }
                    viewState.notifyAdapter(it)
                    viewState.scrollListToPosition(0)
                }, {
                    viewState.showEmptyState()
                    viewState.notifyAdapter(emptyList())

                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showErrorMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            loadSignedAccountsList()
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

    fun onRefreshCalled() {
        loadSignedAccountsList()
    }

    fun onSignedAccountItemClicked(account: Account) {
        viewState.showEditAccountDialog(account.address)
    }

    fun onSaveInstanceState(position: Int) {
        // save list position and restore it after if needed
        savedRvPosition = position
    }

    fun attemptRestoreRvPosition() {
        if (savedRvPosition == Constant.Util.UNDEFINED_VALUE) {
            return
        }

        viewState.scrollListToPosition(savedRvPosition)

        savedRvPosition = Constant.Util.UNDEFINED_VALUE
    }
}