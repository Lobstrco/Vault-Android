package com.lobstr.stellar.vault.presentation.auth.restore_key

import android.text.TextUtils
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.recovery_key.RecoverKeyInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.RecoveryPhraseInfo
import com.lobstr.stellar.vault.presentation.util.Constant.Util.COUNT_MNEMONIC_WORDS_12
import com.lobstr.stellar.vault.presentation.util.Constant.Util.COUNT_MNEMONIC_WORDS_24
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PUBLIC_KEY_LIMIT
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager.ArticleID.RECOVER_ACCOUNT
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import network.lightsail.Language
import network.lightsail.Mnemonic
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecoverKeyFrPresenter @Inject constructor(private val interactor: RecoverKeyInteractor) :
    BasePresenter<RecoverKeyFrView>() {

    companion object {
        private const val DELAY_MILLISECONDS = 200L
    }

    private lateinit var phrases: String

    private val wordlist: List<String> by lazy {
        val resourceName = "wordlist/${Language.ENGLISH.code}.txt"
        val inputStream = Mnemonic::class.java.classLoader?.getResourceAsStream(resourceName)
        checkNotNull(inputStream) { "Word list file not found for language: $Language.ENGLISH" }
        inputStream.bufferedReader().useLines { it.toList() }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.enableNextButton(false)
    }

    fun infoClicked() {
        viewState.showHelpScreen(RECOVER_ACCOUNT)
    }

    fun phrasesChanged(phrases: String) {
        unsubscribeNow()
        // Always disable Next button for prevent wrong state detection.
        viewState.enableNextButton(false)
        unsubscribeOnDestroy(
            Single.timer(DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
                .flatMap {
                    this.phrases = phrases

                    val phraseArray = phrases.split(" ")
                    val wordsInfoList: MutableList<RecoveryPhraseInfo> = mutableListOf()

                    if (phrases.isNotEmpty()) {
                        wordsInfoList.addAll(checkWords(phraseArray, phrases))
                        wordsInfoList.addAll(checkSmallWords(phraseArray, phrases))
                    }

                    val words: List<String> = getPhraseList(phraseArray.toMutableList())

                    return@flatMap Single.fromCallable {
                        Pair(wordsInfoList, words)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val (wordsInfoList, words) = it
                    viewState.changeTextBackground(false)
                    viewState.showInputErrorIfNeeded(wordsInfoList, phrases)

                    // Handling "Next" button state.
                    when {
                        wordsInfoList.isEmpty() -> viewState.enableNextButton(false)
                        haveIncorrectWords(wordsInfoList) -> {
                            viewState.enableNextButton(false)
                            viewState.changeTextBackground(true)
                        }

                        (words.size == COUNT_MNEMONIC_WORDS_12 || words.size == COUNT_MNEMONIC_WORDS_24)
                                && words[words.size - 1].length > 2 -> viewState.enableNextButton(
                            true
                        )
                        else -> viewState.enableNextButton(false)
                    }
                }, {/*Ignore*/ })
        )
    }

    /**
     * Checking if entered words contains in mnemonic words.
     */
    private fun checkWords(phraseArray: List<String>, phrases: String): List<RecoveryPhraseInfo> {
        val wordsInfoList: MutableList<RecoveryPhraseInfo> = mutableListOf()
        val availableWords = wordlist.toString()
        var firstWordIndexPosition: Int = if (phrases.isEmpty()) 0 else phrases.indexOf(phrases[0])

        for (phrase in phraseArray) {
            if (!availableWords.contains(phrase) && !phrase.isEmpty()) {
                wordsInfoList.add(
                    RecoveryPhraseInfo(
                        phrase,
                        firstWordIndexPosition,
                        phrase.length,
                        true
                    )
                )
            } else {
                wordsInfoList.add(
                    RecoveryPhraseInfo(
                        phrase,
                        firstWordIndexPosition,
                        phrase.length,
                        false
                    )
                )
            }
            firstWordIndexPosition += phrase.length + 1
        }

        return wordsInfoList
    }

    /**
     * Checking if entered words has minimum 3 symbol.
     */
    private fun checkSmallWords(
        phraseArray: List<String>,
        phrases: String
    ): List<RecoveryPhraseInfo> {
        val incorrectWords: MutableList<RecoveryPhraseInfo> = mutableListOf()
        var firstWordIndexPosition: Int = if (phrases.isEmpty()) 0 else phrases.indexOf(phrases[0])

        if (phrases[phrases.length - 1] == ' ') {
            for (phrase in phraseArray) {
                if (phrase.length < 3 && phrase.isNotEmpty()) {
                    incorrectWords.add(
                        RecoveryPhraseInfo(
                            phrase,
                            firstWordIndexPosition,
                            phrase.length,
                            true
                        )
                    )
                }
                firstWordIndexPosition += phrase.length + 1
            }
        } else {
            for ((itemCount, phrase) in phraseArray.withIndex()) {
                if (phrase.length < 3 && phrase.isNotEmpty() && itemCount != phraseArray.size - 1) {
                    incorrectWords.add(
                        RecoveryPhraseInfo(
                            phrase,
                            firstWordIndexPosition,
                            phrase.length,
                            true
                        )
                    )
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

    private fun getPhraseList(list: MutableList<String>): List<String> {
        for (position in list.size - 1 downTo 0) {
            if (TextUtils.isEmpty(list[position]) || list[position].equals(" ")) {
                list.removeAt(position)
            }
        }

        return list
    }

    fun btnRecoverClicked() {
        phrases = phrases.trim()
        while (phrases.contains("  ")) {
            phrases = phrases.replace("  ", " ")
        }

        unsubscribeOnDestroy(
            interactor.createAndSaveSecretKey(phrases)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _: String?, _: Throwable? ->
                }
                .subscribe({
                    createAdditionalPublicKeys()
                }, { throwable ->
                    viewState.showProgressDialog(false)
                    viewState.showErrorMessage(R.string.msg_incorrect_mnemonic_phrases)
                })
        )
    }

    private fun createAdditionalPublicKeys() {
        unsubscribeOnDestroy(
            Observable.fromIterable(1 ..< PUBLIC_KEY_LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapSingle { index ->
                    interactor.createAdditionalPublicKey(phrases, index)
                        .map { Pair(index, it) }
                        .onErrorReturnItem(Pair(index, ""))
                }
                .toList()
                .subscribe({ keyData ->
                    checkActiveAccounts(keyData.filter { it.second.isNotEmpty() })
                }, {

                })
        )
    }

    private fun checkActiveAccounts(keyList: List<Pair<Int, String>>) {
        unsubscribeOnDestroy(
            Observable.fromIterable(keyList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapSingle { keyData ->
                    interactor.checkAccount(keyData.second)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { Pair(keyData.first, it.isNotEmpty()) }
                        .onErrorReturnItem(Pair(keyData.first, false))
                }
                .toList()
                .subscribe({ result ->
                    val maxIndex: Int = result.filter { it.second }.let { list ->
                        var index = 0
                        list.forEach {
                            if (it.first > index) index = it.first
                        }
                        index
                    }

                    if (maxIndex > 0) {
                        (1..maxIndex).forEach { index ->
                            interactor.savePublicKeyToList(
                                keyList.find { it.first == index }?.second ?: "",
                                index
                            )
                        }
                    }

                    viewState.showProgressDialog(false)
                    viewState.showPinScreen()
                }, {
                    viewState.showProgressDialog(false)
                    viewState.showPinScreen()
                })
        )
    }
}