package com.lobstr.stellar.vault.domain.stellar

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.Single
import org.stellar.sdk.KeyPair
import org.stellar.sdk.responses.SubmitTransactionResponse

interface StellarRepository {

    fun createKeyPair(mnemonics: CharArray, index: Int): Single<KeyPair>

    fun submitTransaction(signer: KeyPair, envelopXdr: String): Single<SubmitTransactionResponse>

    fun signTransaction(signer: KeyPair, envelopXdr: String): Single<String>

    fun createTransactionItem(envelopXdr: String): Single<TransactionItem>

    fun generate12WordMnemonic(): ArrayList<MnemonicItem>

    fun generate24WordMnemonic(): ArrayList<MnemonicItem>

    fun getTransactionSigners(envelopXdr: String, sourceAccount: String): Single<List<Account>>
}