package com.lobstr.stellar.vault.data.stellar

import com.lobstr.stellar.tsmapper.data.asset.AssetMapper
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset.CanonicalAsset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.AssetType
import com.lobstr.stellar.vault.data.mnemonic.MnemonicsMapper
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.account.Thresholds
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.entities.stellar.SorobanBalanceData
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.tangem.operations.sign.SignResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Single.fromCallable
import network.lightsail.Mnemonic
import org.stellar.sdk.*
import org.stellar.sdk.exception.InvalidSep10ChallengeException
import org.stellar.sdk.requests.AccountsRequestBuilder
import org.stellar.sdk.requests.RequestBuilder
import org.stellar.sdk.responses.AccountResponse
import org.stellar.sdk.xdr.DecoratedSignature
import org.stellar.sdk.xdr.LedgerEntry
import org.stellar.sdk.xdr.LedgerKey
import org.stellar.sdk.xdr.Signature
import java.math.MathContext
import java.util.concurrent.Callable

class StellarRepositoryImpl(
    private val network: Network,
    private val server: Server,
    private val mnemonicsMapper: MnemonicsMapper,
    private val submitMapper: SubmitTransactionMapper,
    private val assetMapper: AssetMapper,
    private val rxErrorUtils: RxErrorUtils
) : StellarRepository {

    override fun generate12WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsStr(Mnemonic().generate(128))
    }

    override fun generate24WordMnemonic(): ArrayList<MnemonicItem> {
        return mnemonicsMapper.transformMnemonicsStr(Mnemonic().generate(256))
    }

    override fun createKeyPair(mnemonics: String, index: Int): Single<KeyPair> {
        return fromCallable(Callable {
            return@Callable KeyPair.fromBip39Seed(
                Mnemonic.toSeed(mnemonics),
                index
            )
        })
    }

    override fun createTransaction(envelopXdr: String): Single<AbstractTransaction> {
        return fromCallable {
            return@fromCallable AbstractTransaction.fromEnvelopeXdr(
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
        }).map {
            submitMapper.transformSubmitResponse(it)
        }.onErrorResumeNext {
            when (it) {
                is org.stellar.sdk.exception.BadRequestException -> {
                    it.problem?.extras?.let { extras ->
                        extras.resultCodes?.let { resultCodes ->
                            if (!resultCodes.transactionResultCode.isNullOrEmpty()) {
                                fromCallable { submitMapper.transformSubmitResponse(extras) }
                            } else {
                                rxErrorUtils.handleSingleRequestHttpError(it)
                            }
                        }
                    } ?: rxErrorUtils.handleSingleRequestHttpError(it)
                }
                else -> rxErrorUtils.handleSingleRequestHttpError(it)
            }
        }
    }

    override fun signTransaction(signer: KeyPair, envelopXdr: String): Single<AbstractTransaction> {
        return fromCallable(Callable {
            val transaction =
                AbstractTransaction.fromEnvelopeXdr(envelopXdr, network)

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
        return AbstractTransaction.fromEnvelopeXdr(xdr, network)
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

    override fun getSorobanBalanceChanges(
        sourceAccount: String,
        envelopXdr: String
    ): Single<List<SorobanBalanceData>> =
        fromCallable(Callable {
            val transaction = when (val tx =
                AbstractTransaction.fromEnvelopeXdr(envelopXdr, network)) {
                is FeeBumpTransaction -> tx.innerTransaction
                is Transaction -> tx
                else -> throw Exception("Unknown transaction type.")
            }

            if (!transaction.isSorobanTransaction) return@Callable emptyList()

            val sim = SorobanServer("https://soroban-rpc.ultrastellar.com").simulateTransaction(transaction)
            val changes = sim.stateChanges
            if (changes.isNullOrEmpty()) return@Callable emptyList()

            val assetChanges: MutableList<SorobanBalanceData> = mutableListOf()
            val oneXLM = "10000000".toBigDecimal()

            for (entryDiff in changes) {
                val ledgerKey = LedgerKey.fromXdrBase64(entryDiff.key)

                when {
                    ledgerKey?.account != null -> {
                        val beforeAccount =
                            LedgerEntry.fromXdrBase64(entryDiff.before)?.data?.account
                        val afterAccount = LedgerEntry.fromXdrBase64(entryDiff.after)?.data?.account

                        if (StrKey.encodeEd25519PublicKey(ledgerKey.account.accountID.accountID.ed25519.uint256) != sourceAccount)
                            continue

                        if ((beforeAccount?.balance?.int64 ?: 0L) == (afterAccount?.balance?.int64
                                ?: 0L)
                        ) continue

                        val beforeAmount = ((beforeAccount?.balance?.int64 ?: 0L).toBigDecimal()
                            .divide(oneXLM, MathContext.DECIMAL128)).stripTrailingZeros().toPlainString()
                        val afterAmount = ((afterAccount?.balance?.int64 ?: 0L).toBigDecimal()
                            .divide(oneXLM, MathContext.DECIMAL128)).stripTrailingZeros().toPlainString()

                        assetChanges.add(
                            SorobanBalanceData(
                                CanonicalAsset(AssetType.ASSET_TYPE_NATIVE, "XLM"),
                                beforeAmount,
                                afterAmount
                            )
                        )
                    }

                    ledgerKey?.trustLine != null -> {
                        val beforeAccount =
                            LedgerEntry.fromXdrBase64(entryDiff.before)?.data?.trustLine
                        val afterAccount =
                            LedgerEntry.fromXdrBase64(entryDiff.after)?.data?.trustLine

                        if (StrKey.encodeEd25519PublicKey(ledgerKey.trustLine.accountID.accountID.ed25519.uint256) != sourceAccount)
                            continue

                        if ((beforeAccount?.balance?.int64 ?: 0L) == (afterAccount?.balance?.int64
                                ?: 0L)
                        ) continue

                        val asset = ledgerKey.trustLine?.asset

                        if (asset == null) continue

                        val beforeAmount = ((beforeAccount?.balance?.int64 ?: 0L).toBigDecimal()
                            .divide(oneXLM, MathContext.DECIMAL128)).stripTrailingZeros().toPlainString()
                        val afterAmount = ((afterAccount?.balance?.int64 ?: 0L).toBigDecimal()
                            .divide(oneXLM, MathContext.DECIMAL128)).stripTrailingZeros().toPlainString()
                        assetChanges.add(
                            SorobanBalanceData(
                                assetMapper.mapTrustLineAsset(asset),
                                beforeAmount,
                                afterAmount
                            )
                        )
                    }
                }
            }

            return@Callable assetChanges
        })
}