package com.lobstr.stellar.vault.domain.pin

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.fcm.FcmHelper
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.ENABLED
import com.lobstr.stellar.vault.presentation.util.Constant.BiometricState.UNKNOWN
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class PinInteractorImpl(
    private val keyStoreRepository: KeyStoreRepository,
    private val accountRepository: AccountRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil,
    private val fcmHelper: FcmHelper
) : PinInteractor {

    override fun checkPinValidation(pin: String): Single<Boolean> {
        return Single.fromCallable {
            val savedPin =
                keyStoreRepository.decryptData(PrefsUtil.PREF_ENCRYPTED_PIN, PrefsUtil.PREF_PIN_IV)
            return@fromCallable !(savedPin.isNullOrEmpty() || savedPin != pin)
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
            )!!
        }
    }

    override fun accountHasToken(): Boolean {
        return !prefsUtil.authToken.isNullOrEmpty()
    }

    /**
     * Check when the user doesn't see Biometric setup screen.
     * @see com.lobstr.stellar.vault.presentation.auth.biometric.BiometricSetUpFragment
     */
    override fun isTouchIdSetUp(): Boolean {
        return prefsUtil.biometricState != UNKNOWN
    }

    override fun isTouchIdEnabled(): Boolean {
        return prefsUtil.biometricState == ENABLED
    }

    override fun getUserPublicKey(): String? {
        return prefsUtil.publicKey
    }

    override fun clearUserData() {
        fcmHelper.unregisterFcm()
        prefsUtil.clearUserPrefs()
        keyStoreRepository.clearAll()
        localDataRepository.clearData()
    }
}