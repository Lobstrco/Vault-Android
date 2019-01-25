package com.lobstr.stellar.vault.domain.stellar

import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import io.reactivex.Single
import org.stellar.sdk.KeyPair
import org.stellar.sdk.responses.SubmitTransactionResponse

interface StellarRepository {

    fun createKeyPair(mnemonics: CharArray, index: Int): Single<KeyPair>

    fun submitTransaction(signer: KeyPair, envelopXdr: String): Single<SubmitTransactionResponse>

    fun signTransaction(signer: KeyPair, envelopXdr: String): Single<String>

    fun generate12WordMnemonic(): ArrayList<MnemonicItem>

    fun generate24WordMnemonic(): ArrayList<MnemonicItem>
}