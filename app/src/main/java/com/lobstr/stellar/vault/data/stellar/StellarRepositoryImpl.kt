package com.lobstr.stellar.vault.data.stellar

import android.content.Context
import com.lobstr.stellar.vault.data.mnemonic.MnemonicsMapper
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.soneso.stellarmnemonics.Wallet
import io.reactivex.Single
import io.reactivex.Single.fromCallable
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import org.stellar.sdk.Transaction
import org.stellar.sdk.responses.SubmitTransactionResponse
import java.util.concurrent.Callable

class StellarRepositoryImpl(
    private val context: Context,
    private val server: Server,
    private val mnemonicsMapper: MnemonicsMapper
) : StellarRepository {

    override fun generate12WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsArray(Wallet.generate12WordMnemonic()!!)
    }

    override fun generate24WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsArray(Wallet.generate24WordMnemonic()!!)
    }

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
        }).onErrorResumeNext {
            LVApplication.sAppComponent.rxErrorUtils.handleSingleRequestHttpError(it)
        }
    }

    override fun signTransaction(signer: KeyPair, envelopXdr: String): Single<String> {
        return fromCallable(Callable<String> {
            val transaction: Transaction = Transaction.fromEnvelopeXdr(envelopXdr)

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(signer)

            // And finally, send it off to Stellar!
            return@Callable transaction.toEnvelopeXdrBase64()
        })
    }
}