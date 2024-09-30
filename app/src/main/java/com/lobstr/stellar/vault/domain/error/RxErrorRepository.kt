package com.lobstr.stellar.vault.domain.error

import io.reactivex.rxjava3.core.Single
import org.stellar.sdk.AbstractTransaction
import org.stellar.sdk.KeyPair


interface RxErrorRepository {

    fun createKeyPair(mnemonics: String, index: Int): Single<KeyPair>

    fun signTransaction(signer: KeyPair, envelopXdr: String): Single<AbstractTransaction>
}