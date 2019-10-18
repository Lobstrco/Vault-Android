package com.lobstr.stellar.vault.domain.import_xdr

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single
import org.stellar.sdk.responses.SubmitTransactionResponse


class ImportXdrInteractorImpl(
    private val transactionRepository: TransactionRepository,
    private val stellarRepository: StellarRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val prefsUtil: PrefsUtil
) : ImportXdrInteractor {

    override fun createTransactionItem(xdr: String): Single<TransactionItem> {
        return stellarRepository.createTransactionItem(xdr)
    }

    override fun confirmTransactionOnHorizon(transaction: String): Single<SubmitTransactionResponse> {
        return getPhrases().flatMap { stellarRepository.createKeyPair(it.toCharArray(), 0) }
            .flatMap { stellarRepository.submitTransaction(it, transaction) }
    }

    override fun confirmTransactionOnServer(
        needAdditionalSignatures: Boolean,
        hash: String?,
        transaction: String
    ): Single<String> {
        return if (needAdditionalSignatures) {
            transactionRepository.submitSignedTransaction(
                AppUtil.getJwtToken(prefsUtil.authToken),
                transaction
            )
        } else {
            transactionRepository.markTransactionAsSubmitted(
                AppUtil.getJwtToken(prefsUtil.authToken),
                hash!!,
                transaction
            )
        }
    }

    override fun getPhrases(): Single<String> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptData(
                PrefsUtil.PREF_ENCRYPTED_PHRASES,
                PrefsUtil.PREF_PHRASES_IV
            )
        }
    }

}