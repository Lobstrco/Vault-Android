package com.lobstr.stellar.vault.domain.transaction_details

import com.lobstr.stellar.vault.data.error.exeption.ForbiddenException
import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.stellar.SubmitTransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single
import org.stellar.sdk.AbstractTransaction

class TransactionDetailsInteractorImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val stellarRepository: StellarRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil
) : TransactionDetailsInteractor {

    override fun hasMnemonics(): Boolean {
        return !prefsUtil.encryptedPhrases.isNullOrEmpty()
    }

    override fun hasTangem(): Boolean {
        return !prefsUtil.tangemCardId.isNullOrEmpty()
    }

    override fun getTangemCardId(): String? {
        return prefsUtil.tangemCardId
    }

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    /**
     * Used for retrieve actual transaction XDR for send it to Horizon.
     * In some cases (when used >1 vault accounts) need retrieve actual XDR.
     *
     * For case when transaction status = IMPORT_XDR - check token and return existing transactionItem after success.
     * @see Constant.Transaction.IMPORT_XDR
     * @param skip Skip action for some cases like for signed transaction via Tangem.
     */
    override fun retrieveActualTransaction(
        transactionItem: TransactionItem,
        skip: Boolean
    ): Single<TransactionItem> {
        return when (transactionItem.status) {
            IMPORT_XDR -> transactionRepository.getTransactionList(
                AppUtil.getJwtToken(prefsUtil.authToken), // Check token.
                null,
                null
            ).flatMap { Single.fromCallable { transactionItem } }
            else ->
                when {
                    skip -> Single.fromCallable { transactionItem }
                    else -> {
                        transactionRepository.retrieveTransaction(
                            AppUtil.getJwtToken(prefsUtil.authToken),
                            transactionItem.hash
                        )
                    }
                }

        }
    }

    override fun confirmTransactionOnHorizon(transaction: AbstractTransaction): Single<SubmitTransactionResult> {
        return stellarRepository.submitTransaction(transaction)
    }

    /**
     * For case when transaction status = IMPORT_XDR - ignore server confirmation errors and return existing transaction xdr.
     * @see Constant.Transaction.IMPORT_XDR
     * @param needAdditionalSignatures For handle transaction confirmation flow.
     */
    override fun confirmTransactionOnServer(
        needAdditionalSignatures: Boolean,
        transactionStatus: Int?,
        hash: String,
        transaction: String
    ): Single<String> {
        return when (needAdditionalSignatures) {
            true -> transactionRepository.submitSignedTransaction(
                AppUtil.getJwtToken(prefsUtil.authToken),
                transaction
            )
            else -> transactionRepository.markTransactionAsSubmitted(
                AppUtil.getJwtToken(prefsUtil.authToken),
                hash,
                transaction
            )
        }.onErrorResumeNext {
            // Ignore errors from Vault server for IMPORT_XDR transaction status.
            when (transactionStatus) {
                IMPORT_XDR -> {
                    Single.fromCallable { transaction }
                }
                else -> {
                    when (it) {
                        // Ignore ForbiddenException for cases when signers were deleted.
                        is ForbiddenException -> {
                            Single.fromCallable { transaction }
                        }
                        else -> {
                            Single.error(it)
                        }
                    }
                }
            }
        }.map { transaction } // Always return original (signed) xdr for the future processing.
    }

    override fun cancelTransaction(hash: String): Single<TransactionItem> {
        return transactionRepository.markTransactionAsCancelled(
            AppUtil.getJwtToken(prefsUtil.authToken),
            hash
        )
    }

    override fun getPhrases(): Single<String> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptData(
                PrefsUtil.PREF_ENCRYPTED_PHRASES,
                PrefsUtil.PREF_PHRASES_IV
            )
        }
    }

    override fun isTrConfirmationEnabled(): Boolean {
        return localDataRepository.getTransactionConfirmationData()[getUserPublicKey()] ?: true
    }

    override fun getSignedAccounts(): Single<List<Account>> {
        return accountRepository.getSignedAccounts(AppUtil.getJwtToken(prefsUtil.authToken))
    }

    override fun getAccountNames(): Map<String, String?> {
        return localDataRepository.getAccountNames()
    }

    override fun getTransactionSigners(xdr: String, sourceAccount: String): Single<AccountResult> {
        return stellarRepository.getTransactionSigners(xdr, sourceAccount).map {
            // Trying to find current vault account in signers to mark it.
            val vaultPublicKey = prefsUtil.publicKey
            it.signers.find { account -> account.address == vaultPublicKey }?.isVaultAccount = true
            it
        }
    }

    override fun getStellarAccount(stellarAddress: String): Single<Account> {
        return accountRepository.getStellarAccount(stellarAddress, "id")
    }

    override fun signTransaction(transaction: String): Single<AbstractTransaction> {
        return getPhrases().flatMap { stellarRepository.createKeyPair(it.toCharArray(), prefsUtil.getCurrentPublicKeyIndex()) }
            .flatMap { stellarRepository.signTransaction(it, transaction) }
    }

    override fun createTransaction(transaction: String): Single<AbstractTransaction> {
        return stellarRepository.createTransaction(transaction)
    }

    override fun getCountSequenceNumber(sourceAccount: String, sequenceNumber: Long): Single<Long> {
        return transactionRepository.getCountSequenceNumber(
            AppUtil.getJwtToken(prefsUtil.authToken),
            sourceAccount,
            sequenceNumber
        )
    }
}