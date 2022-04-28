package com.lobstr.stellar.vault.presentation.home.settings.show_public_key

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.show_public_key.ShowPublicKeyInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ShowPublicKeyPresenter @Inject constructor(
    private val interactor: ShowPublicKeyInteractor
) : BasePresenter<ShowPublicKeyView>() {

    // Reserved for the any others keys (at the moment - Vault Signer Key).
    lateinit var publicKey: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getTitle()
        viewState.setupPublicKey(publicKey)
    }

    private fun getTitle() {
        unsubscribeOnDestroy(
            Single.fromCallable { return@fromCallable interactor.getAccountNames() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val defaultTitle = AppUtil.getString(R.string.text_tv_show_public_key_title)
                    viewState.setupTitle(
                        when {
                            !it[publicKey].isNullOrEmpty() -> it[publicKey]!!
                            interactor.hasTangem() -> defaultTitle
                            else -> """$defaultTitle ${interactor.getUserPublicKeyIndex(publicKey) + 1}"""
                        }
                    )
                }, Throwable::printStackTrace)
        )
    }

    fun copyPublicKeyClicked() {
        viewState.copyToClipBoard(publicKey)
    }
}