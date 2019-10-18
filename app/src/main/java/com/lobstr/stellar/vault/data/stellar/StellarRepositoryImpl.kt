package com.lobstr.stellar.vault.data.stellar

import android.content.Context
import com.lobstr.stellar.vault.data.mnemonic.MnemonicsMapper
import com.lobstr.stellar.vault.data.transaction.TransactionEntityMapper
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.soneso.stellarmnemonics.Wallet
import io.reactivex.Single
import io.reactivex.Single.fromCallable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import org.stellar.sdk.Server
import org.stellar.sdk.Transaction
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import java.security.Provider
import java.security.Security
import java.util.concurrent.Callable

class StellarRepositoryImpl(
    private val context: Context,
    private val network: Network,
    private val server: Server,
    private val mnemonicsMapper: MnemonicsMapper,
    private val transactionEntityMapper: TransactionEntityMapper
) : StellarRepository {

    override fun generate12WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsArray(Wallet.generate12WordMnemonic()!!)
    }

    override fun generate24WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsArray(Wallet.generate24WordMnemonic()!!)
    }

    /**
     * Setup Bouncy Castle as well, because some of the security algorithms used within
     * the library (stellarmnemonic) are supported starting with Java 1.8 only.
     * After remove it.
     */
    override fun createKeyPair(mnemonics: CharArray, index: Int): Single<KeyPair> {
        return fromCallable(Callable<KeyPair> {
            Security.removeProvider("BC")
            Security.addProvider(BouncyCastleProvider() as Provider?)
            return@Callable Wallet.createKeyPair(mnemonics, null, index)
        })
            .doOnEvent { _, _ ->
                // always remove Bouncy Castle after
                Security.removeProvider("BC")
            }
    }

    override fun submitTransaction(
        signer: KeyPair,
        envelopXdr: String
    ): Single<SubmitTransactionResponse> {
        return fromCallable(Callable<SubmitTransactionResponse> {
            val transaction: Transaction = Transaction.fromEnvelopeXdr(envelopXdr, network)

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(signer)

            // And finally, send it off to Stellar!
            return@Callable server.submitTransaction(transaction)
        }).onErrorResumeNext {
            LVApplication.appComponent.rxErrorUtils.handleSingleRequestHttpError(it)
        }
    }

    override fun signTransaction(signer: KeyPair, envelopXdr: String): Single<String> {
        return fromCallable(Callable<String> {
            val transaction: Transaction = Transaction.fromEnvelopeXdr(envelopXdr, network)

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(signer)

            // And finally, send it off to Stellar!
            return@Callable transaction.toEnvelopeXdrBase64()
        })
    }

    /**
     * Used for Create Transaction Item from XDR
     */
    override fun createTransactionItem(envelopXdr: String): Single<TransactionItem> {
        return fromCallable(Callable {
            val transaction = Transaction.fromEnvelopeXdr(envelopXdr, network)

            // Create Transaction Item for handle it in transaction Details
            return@Callable transactionEntityMapper.transformTransactionItem(transaction)
        })
    }

    /**
     * Used for get transaction signers list (exclude VAULT marker key)
     */
    override fun getTransactionSigners(
        envelopXdr: String,
        sourceAccount: String
    ): Single<List<Account>> {
        return Single.create<AccountResponse> {
            try {
                it.onSuccess(server.accounts().account(sourceAccount))
            } catch (exc: Exception) {
                if (it.isDisposed) exc.printStackTrace() else it.onError(exc)
            }
        }
            .map { accountResponse ->
                val transaction = Transaction.fromEnvelopeXdr(envelopXdr, network)

                val accountsList: MutableList<Account> = mutableListOf()

                val signatures = transaction.signatures

                // check signatures list
                if (signatures.isNullOrEmpty()) {
                    return@map accountsList
                }

                // exclude marker key (VAULT)
                val listOfTargetSigners = accountResponse.signers
                    .filter { !it.key.contains("VAULT") }
                    .map { it.key }

                // check verification for each key
                for (key in listOfTargetSigners) {
                    var signed = false

                    for (signature in signatures) {
                        if (KeyPair.fromAccountId(key).verify(
                                transaction.hash(),
                                signature.signature.signature
                            )
                        ) {
                            signed = true
                            break
                        }
                    }

                    accountsList.add(Account(key, signed))
                }

                return@map accountsList.sortedBy { it.signed == true }
            }
            .onErrorResumeNext {
                LVApplication.appComponent.rxErrorUtils.handleSingleRequestHttpError(it)
            }
    }
}