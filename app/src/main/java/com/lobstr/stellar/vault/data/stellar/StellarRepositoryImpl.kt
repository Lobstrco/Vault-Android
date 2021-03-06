package com.lobstr.stellar.vault.data.stellar

import com.lobstr.stellar.vault.data.mnemonic.MnemonicsMapper
import com.lobstr.stellar.vault.data.transaction.TransactionEntityMapper
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.account.Thresholds
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.soneso.stellarmnemonics.Wallet
import com.tangem.commands.SignResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Single.fromCallable
import org.stellar.sdk.*
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.responses.SubmitTransactionResponse
import org.stellar.sdk.xdr.DecoratedSignature
import org.stellar.sdk.xdr.Signature
import java.util.concurrent.Callable

class StellarRepositoryImpl(
    private val network: Network,
    private val server: Server,
    private val mnemonicsMapper: MnemonicsMapper,
    private val transactionEntityMapper: TransactionEntityMapper,
    private val rxErrorUtils: RxErrorUtils
) : StellarRepository {

    override fun generate12WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsArray(Wallet.generate12WordMnemonic()!!)
    }

    override fun generate24WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsArray(Wallet.generate24WordMnemonic()!!)
    }

    override fun createKeyPair(mnemonics: CharArray, index: Int): Single<KeyPair> {
        return fromCallable(Callable {
            return@Callable Wallet.createKeyPair(mnemonics, null, index)
        })
    }

    override fun createTransaction(envelopXdr: String): Single<AbstractTransaction> {
        return fromCallable {
            return@fromCallable AbstractTransaction.fromEnvelopeXdr(envelopXdr, network)
        }
    }

    override fun submitTransaction(
        transaction: AbstractTransaction,
        skipMemoRequiredCheck: Boolean
    ): Single<SubmitTransactionResponse> {
        return fromCallable(Callable {
            return@Callable when(transaction) {
                is Transaction-> server.submitTransaction(transaction, skipMemoRequiredCheck)
                is FeeBumpTransaction -> server.submitTransaction(transaction, skipMemoRequiredCheck)
                else -> throw Exception("Unknown transaction type.")
            }
        }).onErrorResumeNext {
            rxErrorUtils.handleSingleRequestHttpError(it)
        }
    }

    override fun signTransaction(signer: KeyPair, envelopXdr: String): Single<AbstractTransaction> {
        return fromCallable(Callable {
            val transaction = AbstractTransaction.fromEnvelopeXdr(envelopXdr, network)

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(signer)

            return@Callable transaction
        })
    }

    /**
     * Used for Create Transaction Item from XDR.
     */
    override fun createTransactionItem(envelopXdr: String): Single<TransactionItem> {
        return fromCallable(Callable {
            val transaction = AbstractTransaction.fromEnvelopeXdr(envelopXdr, network)
            // Create Transaction Item for handle it in transaction Details
            return@Callable transactionEntityMapper.transformTransactionItem(transaction)
        })
    }

    /**
     * Used for get transaction signers info (exclude VAULT marker key).
     */
    override fun getTransactionSigners(
        envelopXdr: String,
        sourceAccount: String
    ): Single<AccountResult> {
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

                val accountResult = AccountResult(
                    Thresholds(
                        accountResponse.thresholds.lowThreshold,
                        accountResponse.thresholds.medThreshold,
                        accountResponse.thresholds.highThreshold
                    ),
                    accountsList
                )

                val signatures = transaction.signatures ?: return@map accountResult

                // Exclude marker key (VAULT) and accounts with weight = 0.
                val listOfTargetSigners = accountResponse.signers
                    .filter { !it.key.contains("VAULT") && it.weight != 0 }

                // Check verification for each key.
                for (signer in listOfTargetSigners) {
                    var signed = false

                    for (signature in signatures) {
                        if (KeyPair.fromAccountId(signer.key).verify(
                                transaction.hash(),
                                signature.signature.signature
                            )
                        ) {
                            signed = true
                            break
                        }
                    }

                    accountsList.add(Account(signer.key, weight = signer.weight, signed = signed))
                }

                accountResult.signers = accountsList.sortedBy { it.signed == true }
                return@map accountResult
            }
            .onErrorResumeNext {
                rxErrorUtils.handleSingleRequestHttpError(it)
            }
    }

    override fun getPublicKeyFromKeyPair(walletPublicKey: ByteArray?): String? {
        return try {
            val keyPair = KeyPair.fromPublicKey(walletPublicKey)
            return keyPair.accountId
        } catch (exc: RuntimeException) {
            // Handle invalid public key exception.
            null
        }
    }

    override fun getTransactionFromXDR(xdr: String): AbstractTransaction {
        return AbstractTransaction.fromEnvelopeXdr(xdr, network)
    }

    override fun readChallengeTransaction(
        challengeXdr: String,
        serverAccountId: String,
        domainName: String,
        webAuthDomain: String?
    ): Sep10Challenge.ChallengeTransaction? = try {
        Sep10Challenge.readChallengeTransaction(challengeXdr, serverAccountId, network, domainName, webAuthDomain)
    } catch (exc: InvalidSep10ChallengeException) {
        null
    }

    override fun signTransactionWithTangemCardData(
        transaction: AbstractTransaction,
        signResponse: SignResponse,
        accountId: String
    ): String? {
        val signature = Signature()
        signature.signature = signResponse.signature

        val decoratedSignature = DecoratedSignature()

        val keyPair = KeyPair.fromAccountId(accountId)
        decoratedSignature.hint = keyPair.signatureHint
        decoratedSignature.signature = signature

        transaction.signatures.add(decoratedSignature)

        return transaction.toEnvelopeXdrBase64()
    }
}