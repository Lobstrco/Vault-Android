package com.lobstr.stellar.vault.domain.mnemonics

import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import io.reactivex.rxjava3.core.Single


interface MnemonicsInteractor {

    fun generate12WordMnemonics(): ArrayList<MnemonicItem>

    fun getExistingMnemonics(): Single<ArrayList<MnemonicItem>>

    fun getPhrases(): Single<String>

    fun getUserPublicKey(): String?
}