package com.lobstr.stellar.vault.domain.transaction_details

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.domain.transaction.TransactionRepository
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single
import org.stellar.sdk.responses.SubmitTransactionResponse


class TransactionDetailsInteractorImpl(
    private val transactionRepository: TransactionRepository,
    private val stellarRepository: StellarRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val prefsUtil: PrefsUtil
) : TransactionDetailsInteractor {

    override fun confirmTransactionOnHorizon(transaction: String): Single<SubmitTransactionResponse> {
        return getPhrases().flatMap { stellarRepository.createKeyPair(it.toCharArray(), 0) }
            .flatMap { stellarRepository.submitTransaction(it, transaction) }
//            .flatMap {
//                transactionRepository.submitSignedTransaction(
//                    AppUtil.getJwtToken(prefsUtil.authToken),
//                    it.extras?.resultCodes?.transactionResultCode?.equals("tx_bad_auth"),
//                    it.envelopeXdr
//                )
//            }
    }

    override fun confirmTransactionOnServer(submit: Boolean?, transaction: String): Single<String> {
        return transactionRepository.submitSignedTransaction(
            AppUtil.getJwtToken(prefsUtil.authToken),
            submit,
            transaction
        )
    }

    //    fun confirmTransaction(transaction: String){
//        return getPhrases().flatMap { stellarRepository.createKeyPair(it.toCharArray(), 0) }
//            .flatMap { stellarRepository.submitTransaction(it, transaction) }
//            .flatMap {
//                transactionRepository.submitSignedTransaction(
//                    AppUtil.getJwtToken(prefsUtil.authToken),
//                    it.extras?.resultCodes?.transactionResultCode?.equals("tx_bad_auth"),
//                    it.envelopeXdr
//                )
//            }
//    }

    override fun cancelTransaction(hash: String): Single<TransactionItem> {
        return transactionRepository.markTransactionAsCancelled(AppUtil.getJwtToken(prefsUtil.authToken), hash)
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