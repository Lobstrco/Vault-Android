package com.lobstr.stellar.vault.domain.mnemonics

import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import io.reactivex.Single


interface MnemonicsInteractor {

    fun generate12WordMnemonics(): ArrayList<MnemonicItem>

    fun getExistingMnemonics(): Single<ArrayList<MnemonicItem>>

    fun getPhrases(): Single<String>
}