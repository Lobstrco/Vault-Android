package com.lobstr.stellar.vault.domain.transaction_details

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single
import org.stellar.sdk.AbstractTransaction
import org.stellar.sdk.responses.SubmitTransactionResponse


class TransactionDetailsInteractorImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val stellarRepository: StellarRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val prefsUtil: PrefsUtil
) : TransactionDetailsInteractor {

    /**
     * Used for retrieve actual transaction XDR for send it to Horizon.
     * In some cases (when used >1 vault accounts) need retrieve actual XDR.
     *
     * For case when transaction status = IMPORT_XDR - return existing transactionItem.
     * @see Constant.Transaction.IMPORT_XDR
     */
    override fun retrieveActualTransaction(transactionItem: TransactionItem): Single<TransactionItem> {
        return when (transactionItem.status) {
            IMPORT_XDR -> Single.fromCallable { transactionItem }
            else -> transactionRepository.retrieveTransaction(
                AppUtil.getJwtToken(prefsUtil.authToken),
                transactionItem.hash
            )
        }
    }

    override fun confirmTransactionOnHorizon(transaction: AbstractTransaction): Single<SubmitTransactionResponse> {
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
        hash: String?,
        transaction: String
    ): Single<String> {
        return when (needAdditionalSignatures) {
            true -> transactionRepository.submitSignedTransaction(
                AppUtil.getJwtToken(prefsUtil.authToken),
                transaction
            )
            else -> transactionRepository.markTransactionAsSubmitted(
                AppUtil.getJwtToken(prefsUtil.authToken),
                hash!!,
                transaction
            )
        }.onErrorResumeNext {
            // Ignore errors from Vault server for IMPORT_XDR transaction status.
            when (transactionStatus) {
                IMPORT_XDR -> {
                    Single.fromCallable { transaction }
                }
                else -> {
                    Single.error(it)
                }
            }
        }
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
        return prefsUtil.isTrConfirmationEnabled
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
        return getPhrases().flatMap { stellarRepository.createKeyPair(it.toCharArray(), 0) }
            .flatMap { stellarRepository.signTransaction(it, transaction) }
    }
}