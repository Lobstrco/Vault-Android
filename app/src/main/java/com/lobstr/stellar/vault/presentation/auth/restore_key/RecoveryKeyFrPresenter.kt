package com.lobstr.stellar.vault.presentation.auth.restore_key

import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.recovery_key.RecoveryKeyInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.RecoveryPhraseInfo
import com.lobstr.stellar.vault.presentation.dagger.module.recovery_key.RecoveryKeyModule
import com.lobstr.stellar.vault.presentation.util.Constant.Util.COUNT_MNEMONIC_WORDS_12
import com.lobstr.stellar.vault.presentation.util.Constant.Util.COUNT_MNEMONIC_WORDS_24
import com.soneso.stellarmnemonics.mnemonic.MnemonicException
import com.soneso.stellarmnemonics.mnemonic.WordList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@InjectViewState
class RecoveryKeyFrPresenter : BasePresenter<RecoveryKeyFrView>() {

    @Inject
    lateinit var interactor: RecoveryKeyInteractor

    private lateinit var phrases: String

    init {
        LVApplication.sAppComponent.plusRecoveryKeyComponent(RecoveryKeyModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.enableNextButton(false)
    }

    fun infoClicked() {
        viewState.showHelpScreen()
    }

    fun phrasesChanged(phrases: String) {
        this.phrases = phrases
        val phraseArray = phrases.split(" ")
        val wordsInfoList: MutableList<RecoveryPhraseInfo> = mutableListOf()

        if (phrases.isNotEmpty()) {
            wordsInfoList.addAll(checkWords(phraseArray, phrases))
            wordsInfoList.addAll(checkSmallWords(phraseArray, phrases))
        }

        viewState.changeTextBackground(false)
        viewState.showInputErrorIfNeeded(wordsInfoList, phrases)

        // Handling "Next" button state.
        val words: List<String> = getPhraseList(phraseArray.toMutableList())
        when {
            wordsInfoList.isEmpty() -> viewState.enableNextButton(false)
            haveIncorrectWords(wordsInfoList) -> {
                viewState.enableNextButton(false)
                viewState.changeTextBackground(true)
            }
            haveMatchingWords(words) -> {
                viewState.enableNextButton(false)
                viewState.changeTextBackground(true)
            }
            (words.size == COUNT_MNEMONIC_WORDS_12 || words.size == COUNT_MNEMONIC_WORDS_24)
                    && words[words.size - 1].length > 2 -> viewState.enableNextButton(true)
            else -> viewState.enableNextButton(false)
        }
    }

    /**
     * Checking if entered words contains in mnemonic words.
     */
    private fun checkWords(phraseArray: List<String>, phrases: String): List<RecoveryPhraseInfo> {
        val wordsInfoList: MutableList<RecoveryPhraseInfo> = mutableListOf()
        val availableWords = WordList.ENGLISH.words.toString()
        var firstWordIndexPosition: Int = if (phrases.isEmpty()) 0 else phrases.indexOf(phrases[0])

        for (phrase in phraseArray) {
            if (!availableWords.contains(phrase) && !phrase.isEmpty()) {
                wordsInfoList.add(RecoveryPhraseInfo(phrase, firstWordIndexPosition, phrase.length, true))
            } else {
                wordsInfoList.add(RecoveryPhraseInfo(phrase, firstWordIndexPosition, phrase.length, false))
            }
            firstWordIndexPosition += phrase.length + 1
        }

        return wordsInfoList
    }

    /**
     * Checking if entered words has minimum 3 symbol.
     */
    private fun checkSmallWords(phraseArray: List<String>, phrases: String): List<RecoveryPhraseInfo> {
        val incorrectWords: MutableList<RecoveryPhraseInfo> = mutableListOf()
        var firstWordIndexPosition: Int = if (phrases.isEmpty()) 0 else phrases.indexOf(phrases[0])

        if (phrases[phrases.length - 1] == ' ') {
            for (phrase in phraseArray) {
                if (phrase.length < 3 && phrase.isNotEmpty()) {
                    incorrectWords.add(RecoveryPhraseInfo(phrase, firstWordIndexPosition, phrase.length, true))
                }
                firstWordIndexPosition += phrase.length + 1
            }
        } else {
            for ((itemCount, phrase) in phraseArray.withIndex()) {
                if (phrase.length < 3 && phrase.isNotEmpty() && itemCount != phraseArray.size - 1) {
                    incorrectWords.add(RecoveryPhraseInfo(phrase, firstWordIndexPosition, phrase.length, true))
                }
                firstWordIndexPosition += phrase.length + 1
            }
        }

        return incorrectWords
    }

    private fun haveIncorrectWords(wordsInfoListRecovery: MutableList<RecoveryPhraseInfo>): Boolean {
        for (wordInfo in wordsInfoListRecovery) {
            if (wordInfo.incorrect) {
                return true
            }
        }

        return false
    }

    private fun haveMatchingWords(phrases: List<String>): Boolean {
        for (firstPosition in 0 until phrases.size - 1) {
            for (secondPosition in firstPosition + 1 until phrases.size) {
                if (phrases[secondPosition].equals(phrases[firstPosition]) && secondPosition != firstPosition) {
                    return true
                }
            }
        }

        return false
    }

    private fun getPhraseList(list: MutableList<String>): List<String> {
        for (position in list.size - 1 downTo 0) {
            if (TextUtils.isEmpty(list[position]) || list[position].equals(" ")) {
                list.removeAt(position)
            }
        }

        return list
    }

    fun btnRecoveryClicked() {
        phrases = phrases.trim()
        while (phrases.contains("  ")) {
            phrases = phrases.replace("  ", " ")
        }

        unsubscribeOnDestroy(
            interactor.createAndSaveSecretKey(phrases.toCharArray())
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
                        viewState.showErrorMessage(R.string.text_error_incorrect_mnemonic)
                    }
                })
        )
    }
}