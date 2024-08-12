package com.lobstr.stellar.vault.data.error

import com.lobstr.stellar.vault.domain.error.RxErrorRepository
import com.soneso.stellarmnemonics.Wallet
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Single.fromCallable
import org.stellar.sdk.AbstractTransaction
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Network
import java.util.concurrent.Callable


class RxErrorRepositoryImpl(
    private val network: Network
) : RxErrorRepository {

    override fun createKeyPair(mnemonics: CharArray, index: Int): Single<KeyPair> {
        return fromCallable(Callable {
            return@Callable Wallet.createKeyPair(mnemonics, null, index)
        })
    }

    override fun signTransaction(signer: KeyPair, envelopXdr: String): Single<AbstractTransaction> {
        return fromCallable(Callable {
            val transaction = AbstractTransaction.fromEnvelopeXdr(envelopXdr, network)

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(signer)

            return@Callable transaction
        })
    }
}