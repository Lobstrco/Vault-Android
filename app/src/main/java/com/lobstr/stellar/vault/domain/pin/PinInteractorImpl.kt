package com.lobstr.stellar.vault.domain.pin

import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.ENABLED
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.UNKNOWN
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Completable
import io.reactivex.Single

class PinInteractorImpl(
    private val keyStoreRepository: KeyStoreRepository,
    private val prefsUtil: PrefsUtil
) : PinInteractor {

    override fun checkPinValidation(pin: String): Single<Boolean> {
        return Single.fromCallable {
            val savedPin = keyStoreRepository.decryptData(PrefsUtil.PREF_ENCRYPTED_PIN, PrefsUtil.PREF_PIN_IV)
            return@fromCallable !(savedPin.isNullOrEmpty() || !savedPin.equals(pin))
        }
    }

    override fun savePhrases(phrases: String): Completable {
        return Completable.fromCallable {
            return@fromCallable keyStoreRepository.encryptData(
                phrases, PrefsUtil.PREF_ENCRYPTED_PHRASES, PrefsUtil.PREF_PHRASES_IV
            )
        }
    }

    override fun savePin(pin: String): Completable {
        return Completable.fromCallable {
            keyStoreRepository.clear(PrefsUtil.PREF_ENCRYPTED_PIN)
            return@fromCallable keyStoreRepository.encryptData(
                pin, PrefsUtil.PREF_ENCRYPTED_PIN, PrefsUtil.PREF_PIN_IV
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

    override fun accountHasSigners(): Boolean {
        return prefsUtil.accountHasSigners
    }

    /**
     * Check when the user doesn't see Finger Print setup screen
     * @see com.lobstr.stellar.vault.presentation.auth.touch_id.FingerprintSetUpFragment
     */
    override fun isTouchIdSetUp(): Boolean {
        return prefsUtil.biometricState != UNKNOWN
    }

    override fun isTouchIdEnabled(): Boolean {
        return prefsUtil.biometricState == ENABLED
    }

    override fun clearUserData() {
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clearAll()
    }
}