package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.confirm_mnemonics.ConfirmMnemonicsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.confirm_mnemonics.ConfirmMnemonicsModule
import com.lobstr.stellar.vault.presentation.util.Constant
import com.soneso.stellarmnemonics.mnemonic.MnemonicException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class ConfirmMnemonicsPresenter(private val mnemonicsArray: CharArray) : BasePresenter<ConfirmMnemonicsView>() {

    @Inject
    lateinit var interactor: ConfirmMnemonicsInteractor

    init {
        LVApplication.sAppComponent.plusConfirmMnemonicsComponent(ConfirmMnemonicsModule()).inject(this)
    }

    // List of original mnemonics for compare
    private var mnemonicsToSelectInitialList = listOf<String>()

    // List of mnemonics in confirmation (top) section
    private var mnemonicsToConfirmList = mutableListOf<String>()

    // List of mnemonics in selection (bottom) section
    private var mnemonicsToSelectList = mutableListOf<String>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        prepareShuffledList()
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
    }

    /**
     * Create shuffled list for selection (bottom) section and save original
     */
    private fun prepareShuffledList() {
        val mnemonicsStr = String(mnemonicsArray)
        mnemonicsToSelectInitialList = mnemonicsStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
        mnemonicsToSelectList = mnemonicsToSelectInitialList.toMutableList()
        mnemonicsToSelectList.shuffle()
    }

    fun nextClicked() {
        // FIXME remove in future for debug
        if (BuildConfig.BUILD_TYPE == Constant.BuildType.RELEASE) {
            if (mnemonicsToConfirmList.size < mnemonicsToSelectInitialList.size) {
                viewState.showMessage(R.string.msg_not_all_words_entered)
                return
            }

            if (mnemonicsToSelectInitialList.toString() != mnemonicsToConfirmList.toString()) {
                viewState.showMessage(R.string.msg_phrase_dont_fit)
                return
            }
        }

        unsubscribeOnDestroy(
            interactor.createAndSaveSecretKey(mnemonicsArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog()
                }
                .doOnEvent { _: String?, _: Throwable? ->
                    viewState.dismissProgressDialog()
                }
                .subscribe({
                    viewState.showPinScreen()
                }, { throwable ->
                    if (throwable is MnemonicException) {
                        viewState.showMessage(R.string.text_error_incorrect_mnemonic)
                    }
                })
        )
    }

    /**
     * Remove mnemonic item from confirmation section and add it to selection section
     */
    fun mnemonicItemToConfirmClicked(position: Int, value: String) {
        mnemonicsToConfirmList.removeAt(position)
        mnemonicsToSelectList.add(value)
        viewState.setupMnemonicsToConfirm(mnemonicsToConfirmList)
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
    }

    /**
     * Remove mnemonic item from selection section and add it to confirmation section
     */
    fun mnemonicItemToSelectClicked(position: Int, value: String) {
        mnemonicsToSelectList.removeAt(position)
        mnemonicsToConfirmList.add(value)
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
        viewState.setupMnemonicsToConfirm(mnemonicsToConfirmList)
    }
}