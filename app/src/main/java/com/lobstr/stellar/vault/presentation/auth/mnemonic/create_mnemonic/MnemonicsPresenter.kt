package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.mnemonics.MnemonicsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.mnemonics.MnemonicsModule
import com.soneso.stellarmnemonics.Wallet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class MnemonicsPresenter(private val generate: Boolean) : BasePresenter<MnemonicsView>() {

    @Inject
    lateinit var interactor: MnemonicsInteractor

    init {
        LVApplication.sAppComponent.plusMnemonicsComponent(MnemonicsModule()).inject(this)
    }

    private lateinit var mnemonicsArray: CharArray

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (generate) {
            setupMnemonics(Wallet.generate12WordMnemonic())
        } else {
            viewState.setupToolbarTitle(R.string.mnemonics_title)
            viewState.setActionBtnVisibility(false)
            getExistingMnemonics()
        }
    }

    private fun getExistingMnemonics() {
        unsubscribeOnDestroy(
            interactor.getPhrases()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setupMnemonics(it.toCharArray())
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun setupMnemonics(mnemonics: CharArray) {
        mnemonicsArray = mnemonics
        val mnemonicsStr = String(mnemonics)
        viewState.setupMnemonics(mnemonicsStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() })
    }

    fun nextClicked() {
        viewState.showConfirmationScreen(mnemonicsArray)
    }

    fun clipToBordClicked() {
        if (mnemonicsArray.isEmpty()) {
            return
        }

        viewState.copyToClipBoard(String(mnemonicsArray))
    }
}