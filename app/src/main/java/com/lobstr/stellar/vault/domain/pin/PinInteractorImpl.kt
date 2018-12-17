package com.lobstr.stellar.vault.domain.pin

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Completable
import io.reactivex.Single
import java.security.KeyPair

class PinInteractorImpl(
    private val keyStoreRepository: KeyStoreRepository,
    private val prefUtil: PrefsUtil
) : PinInteractor {

    override fun checkPinValidation(pin: String): Single<KeyPair> {
        return Single.fromCallable {
            var keyPair = keyStoreRepository.getAsymmetricKeyPair(pin)
            if (keyPair == null) {
                keyPair = KeyPair(null, null)
            }
            return@fromCallable keyPair
        }
    }

    override fun saveSecretKey(pin: String, secretKey: String): Completable {
        return Completable.fromCallable {
            val encryptedString = keyStoreRepository.encryptSecretKey(pin, secretKey)
            prefUtil.encryptedKey = encryptedString
            return@fromCallable encryptedString
        }
    }

    override fun getSecretKey(pin: String): Single<String> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptSecretKey(pin, prefUtil.encryptedKey!!)
        }
    }

    override fun clear() {
        keyStoreRepository.clear()
    }
}