package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic

import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.confirm_mnemonics.ConfirmMnemonicsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.confirm_mnemonics.ConfirmMnemonicsModule
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.COUNT_MNEMONIC_WORDS_12
import com.soneso.stellarmnemonics.mnemonic.MnemonicException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class ConfirmMnemonicsPresenter(private val mnemonicsInitialList: List<MnemonicItem>) :
    BasePresenter<ConfirmMnemonicsView>() {

    @Inject
    lateinit var interactor: ConfirmMnemonicsInteractor

    init {
        LVApplication.sAppComponent.plusConfirmMnemonicsComponent(ConfirmMnemonicsModule()).inject(this)
    }

    // List of mnemonics in confirmation (top) section
    private val mnemonicsToConfirmList = mutableListOf<MnemonicItem>()

    // List of mnemonics in selection (bottom) section
    private val mnemonicsToSelectList =
        mnemonicsInitialList.map { MnemonicItem(it.value) }.toMutableList()

    private lateinit var mnemonicsInitialStr: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        prepareShuffledList()
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
    }

    /**
     * Create shuffled list for selection (bottom) section and save original
     */
    private fun prepareShuffledList() {
        mnemonicsInitialStr = mnemonicsInitialList.joinToString(" ") { it -> it.value }
        mnemonicsToSelectList.shuffle()
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }

    fun nextClicked() {
        // FIXME remove in future for debug
        if (BuildConfig.BUILD_TYPE == Constant.BuildType.RELEASE) {
            if (mnemonicsToConfirmList.size < mnemonicsInitialList.size) {
                viewState.showMessage(R.string.msg_not_all_words_entered)
                return
            }

            if (mnemonicsInitialStr != mnemonicsToConfirmList.joinToString(" ") { it -> it.value }) {
                viewState.showMessage(R.string.msg_phrase_dont_fit)
                return
            }
        }

        unsubscribeOnDestroy(
            interactor.createAndSaveSecretKey(mnemonicsInitialStr.toCharArray())
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
        viewState.setActionButtonEnabled(false)
        mnemonicsToConfirmList.removeAt(position)
        mnemonicsToSelectList.find { it.value == value }?.hide = false
        viewState.setupMnemonicsToConfirm(mnemonicsToConfirmList)
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
    }

    /**
     * Remove mnemonic item from selection section (set it hide = true) and add it to confirmation section
     */
    fun mnemonicItemToSelectClicked(position: Int, value: String) {
        mnemonicsToSelectList[position].hide = true
        mnemonicsToConfirmList.add(MnemonicItem(value))
        viewState.setActionButtonEnabled(mnemonicsToConfirmList.size == COUNT_MNEMONIC_WORDS_12)
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
        viewState.setupMnemonicsToConfirm(mnemonicsToConfirmList)
    }
}