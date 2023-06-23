package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic

import com.lobstr.stellar.vault.BuildConfig
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.confirm_mnemonics.ConfirmMnemonicsInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Util.COUNT_MNEMONIC_WORDS_12
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager.ArticleID.RECOVERY_PHRASE
import com.soneso.stellarmnemonics.mnemonic.MnemonicException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ConfirmMnemonicsPresenter @Inject constructor(private val interactor: ConfirmMnemonicsInteractor) :
    BasePresenter<ConfirmMnemonicsView>() {

    lateinit var mnemonicsInitialList: List<MnemonicItem>

    // List of mnemonics in confirmation (top) section
    private val mnemonicsToConfirmList = mutableListOf<MnemonicItem>()

    // List of mnemonics in selection (bottom) section
    private lateinit var mnemonicsToSelectList: MutableList<MnemonicItem>

    private lateinit var mnemonicsInitialStr: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        mnemonicsToSelectList = mnemonicsInitialList.map { MnemonicItem(it.value) }.toMutableList()

        prepareShuffledList()
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
    }

    /**
     * Create shuffled list for selection (bottom) section and save original.
     */
    private fun prepareShuffledList() {
        mnemonicsInitialStr = mnemonicsInitialList.joinToString(" ") { it.value }
        mnemonicsToSelectList.shuffle()
    }

    fun infoClicked() {
        viewState.showHelpScreen(RECOVERY_PHRASE)
    }

    fun btnNextClicked() {
        if (BuildConfig.BUILD_TYPE == Constant.BuildType.RELEASE) {
            if (mnemonicsToConfirmList.size < mnemonicsInitialList.size) {
                viewState.showMessage(R.string.confirm_mnemonics_msg_not_all_words_entered)
                return
            }

            if (mnemonicsInitialStr != mnemonicsToConfirmList.joinToString(" ") { it.value }) {
                viewState.showMessage(R.string.confirm_mnemonics_msg_wrong_order)
                return
            }
        }

        unsubscribeOnDestroy(
            interactor.createAndSaveSecretKey(mnemonicsInitialStr.toCharArray())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _: String?, _: Throwable? ->
                    viewState.showProgressDialog(false)
                }
                .subscribe({
                    viewState.showPinScreen()
                }, { throwable ->
                    if (throwable is MnemonicException) {
                        viewState.showMessage(R.string.msg_incorrect_mnemonic_phrases)
                    }
                })
        )
    }

    /**
     * Remove mnemonic items from confirmation section and restore selection section.
     */
    fun btnClearClicked() {
        viewState.setActionButtonEnabled(false)
        mnemonicsToConfirmList.clear()
        mnemonicsToSelectList.forEach { it.hide = false }
        viewState.setupMnemonicsToConfirm(mnemonicsToConfirmList)
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
    }

    /**
     * Remove mnemonic item from confirmation section and add it to selection section.
     */
    fun mnemonicItemToConfirmClicked(position: Int, value: String) {
        viewState.setActionButtonEnabled(false)
        mnemonicsToConfirmList.removeAt(position)
        mnemonicsToSelectList.find { it.value == value }?.hide = false
        viewState.setupMnemonicsToConfirm(mnemonicsToConfirmList)
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
    }

    /**
     * Remove mnemonic item from selection section (set it hide = true) and add it to confirmation section.
     */
    fun mnemonicItemToSelectClicked(position: Int, value: String) {
        mnemonicsToSelectList[position].hide = true
        mnemonicsToConfirmList.add(MnemonicItem(value))
        viewState.setActionButtonEnabled(mnemonicsToConfirmList.size == COUNT_MNEMONIC_WORDS_12)
        viewState.setupMnemonicsToSelect(mnemonicsToSelectList)
        viewState.setupMnemonicsToConfirm(mnemonicsToConfirmList)
    }
}