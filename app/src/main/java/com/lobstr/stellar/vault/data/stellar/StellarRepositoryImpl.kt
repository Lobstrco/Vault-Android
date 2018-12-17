package com.lobstr.stellar.vault.data.stellar

import android.content.Context
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.soneso.stellarmnemonics.Wallet
import io.reactivex.Single
import io.reactivex.Single.fromCallable
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import org.stellar.sdk.Transaction
import org.stellar.sdk.responses.SubmitTransactionResponse
import java.util.concurrent.Callable

class StellarRepositoryImpl(private val context: Context, private val server: Server) : StellarRepository {

    override fun createKeyPair(mnemonics: CharArray, index: Int): Single<KeyPair> {
        return fromCallable(Callable<KeyPair> {
            return@Callable Wallet.createKeyPair(mnemonics, null, index)
        })
    }

    override fun submitTransaction(signer: KeyPair, envelopXdr: String): Single<SubmitTransactionResponse> {
        return fromCallable(Callable<SubmitTransactionResponse> {
            val transaction: Transaction = Transaction.fromEnvelopeXdr(envelopXdr)

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(signer)

            // And finally, send it off to Stellar!
            return@Callable server.submitTransaction(transaction)
        })
    }
}