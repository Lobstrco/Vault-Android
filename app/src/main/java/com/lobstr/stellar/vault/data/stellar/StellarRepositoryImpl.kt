package com.lobstr.stellar.vault.data.stellar

import com.lobstr.stellar.vault.data.mnemonic.MnemonicsMapper
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.account.Thresholds
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.soneso.stellarmnemonics.Wallet
import com.tangem.operations.sign.SignResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Single.fromCallable
import org.stellar.sdk.*
import org.stellar.sdk.requests.AccountsRequestBuilder
import org.stellar.sdk.requests.RequestBuilder
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.xdr.DecoratedSignature
import org.stellar.sdk.xdr.Signature
import java.util.concurrent.Callable

class StellarRepositoryImpl(
    private val accountConverter: AccountConverter,
    private val network: Network,
    private val server: Server,
    private val mnemonicsMapper: MnemonicsMapper,
    private val submitMapper: SubmitTransactionMapper,
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
            return@fromCallable AbstractTransaction.fromEnvelopeXdr(
                accountConverter,
                envelopXdr,
                network
            )
        }
    }

    override fun submitTransaction(
        transaction: AbstractTransaction,
        skipMemoRequiredCheck: Boolean
    ): Single<SubmitTransactionResult> {
        return fromCallable(Callable {
            return@Callable when (transaction) {
                is Transaction -> server.submitTransaction(transaction, skipMemoRequiredCheck)
                is FeeBumpTransaction -> server.submitTransaction(
                    transaction,
                    skipMemoRequiredCheck
                )
                else -> throw Exception("Unknown transaction type.")
            }
        }).onErrorResumeNext {
            rxErrorUtils.handleSingleRequestHttpError(it)
        }.map {
            submitMapper.transformSubmitResponse(it)
        }
    }

    override fun signTransaction(signer: KeyPair, envelopXdr: String): Single<AbstractTransaction> {
        return fromCallable(Callable {
            val transaction =
                AbstractTransaction.fromEnvelopeXdr(accountConverter, envelopXdr, network)

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(signer)

            return@Callable transaction
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
                it.onSuccess(server.accounts().account(AppUtil.decodeAccountStr(sourceAccount)))
            } catch (exc: Exception) {
                if (it.isDisposed) exc.printStackTrace() else it.onError(exc)
            }
        }
            .map { accountResponse ->
                val transaction = Transaction.fromEnvelopeXdr(accountConverter, envelopXdr, network)

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

    override fun getPublicKeyFromKeyPair(walletPublicKey: String?): ByteArray? {
        return try {
            val keyPair = KeyPair.fromAccountId(walletPublicKey)
            return keyPair.publicKey
        } catch (exc: RuntimeException) {
            // Handle invalid public key exception.
            null
        }
    }

    override fun getTransactionFromXDR(xdr: String): AbstractTransaction {
        return AbstractTransaction.fromEnvelopeXdr(accountConverter, xdr, network)
    }

    override fun readChallengeTransaction(
        challengeXdr: String,
        serverAccountId: String,
        domainName: String,
        webAuthDomain: String?
    ): Sep10Challenge.ChallengeTransaction? = try {
        Sep10Challenge.readChallengeTransaction(
            challengeXdr,
            serverAccountId,
            network,
            domainName,
            webAuthDomain
        )
    } catch (exc: InvalidSep10ChallengeException) {
        null
    }

    override fun signTransactionWithTangemCardData(
        transaction: AbstractTransaction,
        signResponse: SignResponse,
        accountId: String
    ): String? {
        val signature = Signature()
        signature.signature = signResponse.signatures[0]

        val decoratedSignature = DecoratedSignature()

        val keyPair = KeyPair.fromAccountId(accountId)
        decoratedSignature.hint = keyPair.signatureHint
        decoratedSignature.signature = signature

        transaction.addSignature(decoratedSignature)

        return transaction.toEnvelopeXdrBase64()
    }

    /**
     * Use for simple encoding. E.g for the retrieve String representation of Public Key from the array of bytes.
     * @param data Array of bytes.
     * @return String or null in Exception case.
     */
    override fun encodeStellarAccountId(data: ByteArray?): String? {
        return try {
            StrKey.encodeEd25519PublicKey(data)
        } catch (exc: Exception) {
            null
        }
    }

    override fun getAccountsForSigner(
        signer: String,
        cursor: String?,
        order: RequestBuilder.Order,
        limit: Int?
    ): Single<List<Account>> {
        return fromCallable {
            createAccountsBuilder(cursor, order, limit).forSigner(signer).execute()
        }
            .map {
                mutableListOf<Account>().apply {
                    it.records.forEach { account ->
                        add(Account(account.accountId))
                    }
                }.toList()
            }
    }

    private fun createAccountsBuilder(
        cursor: String? = null,
        order: RequestBuilder.Order = RequestBuilder.Order.ASC,
        limit: Int? = 1
    ): AccountsRequestBuilder {
        return server.accounts().apply {
            cursor?.let { cursor(cursor) }
            order(order)
            limit?.let { limit(limit) }
        }
    }
}