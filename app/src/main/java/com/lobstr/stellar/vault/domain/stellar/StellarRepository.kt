package com.lobstr.stellar.vault.domain.stellar

import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.tangem.commands.SignResponse
import io.reactivex.rxjava3.core.Single
import org.stellar.sdk.AbstractTransaction
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Sep10Challenge
import org.stellar.sdk.responses.SubmitTransactionResponse

interface StellarRepository {

    fun createKeyPair(mnemonics: CharArray, index: Int): Single<KeyPair>

    fun createTransaction(envelopXdr: String): Single<AbstractTransaction>

    fun submitTransaction(
        transaction: AbstractTransaction,
        skipMemoRequiredCheck: Boolean = true
    ): Single<SubmitTransactionResponse>

    fun signTransaction(signer: KeyPair, envelopXdr: String): Single<AbstractTransaction>

    /**
     * Used for Create Transaction Item from XDR.
     */
    fun createTransactionItem(envelopXdr: String): Single<TransactionItem>

    fun generate12WordMnemonic(): ArrayList<MnemonicItem>

    fun generate24WordMnemonic(): ArrayList<MnemonicItem>

    /**
     * Used for get transaction signers info (exclude VAULT marker key).
     */
    fun getTransactionSigners(envelopXdr: String, sourceAccount: String): Single<AccountResult>

    fun getPublicKeyFromKeyPair(walletPublicKey: ByteArray?): String?

    fun getTransactionFromXDR(xdr: String): AbstractTransaction

    fun readChallengeTransaction(challengeXdr: String, serverAccountId: String, domainName: String, webAuthDomain: String?): Sep10Challenge.ChallengeTransaction?

    fun signTransactionWithTangemCardData(
        transaction: AbstractTransaction,
        signResponse: SignResponse,
        accountId: String
    ): String?
}