package com.lobstr.stellar.vault.data.error

import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.ExpiredSignatureException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException.Action.AUTH_REQUIRED
import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.domain.error.RxErrorRepository
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class RxErrorUtilsImpl(
    private val exceptionMapper: ExceptionMapper,
    private val vaultAuthApi: VaultAuthApi,
    private val rxErrorRepository: RxErrorRepository,
    private val keyStoreRepository: KeyStoreRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil
) : RxErrorUtils {

    override fun <T> handleObservableRequestHttpError(
        throwable: Throwable,
        tag: Int,
        publicKey: String?
    ): Observable<T> {
        val ex = exceptionMapper.transformHttpToDomain(throwable)

        if (ex is ExpiredSignatureException) {
            return refreshObservableAuthentication(publicKey)
        }

        // Token renewal is handled in TokenExpiryAuthenticator. Here we can just logout user.
        return if (ex is UserNotAuthorizedException) {
            when (tag) {
                Constant.ApiRequestTag.REFRESH_AUTH -> Observable.error(ex)
                else -> refreshObservableAuthentication(publicKey)
            }
        } else {
            Observable.error(ex)
        }
    }

    private fun <T> refreshObservableAuthentication(publicKey: String?): Observable<T> {
        // Throw UserNotAuthorizedException with AUTH_REQUIRED action for non mnemonics case.
        if (prefsUtil.encryptedPhrases.isNullOrEmpty()) {
            return Observable.error(
                UserNotAuthorizedException(
                    "User Not Authorized",
                    AUTH_REQUIRED
                )
            )
        }

        val key = publicKey ?: prefsUtil.publicKey
        return vaultAuthApi.getChallenge(key)
            .flatMap { apiGetChallenge ->
                getPhrases().flatMap {
                    rxErrorRepository.createKeyPair(
                        it.toCharArray(),
                        prefsUtil.getPublicKeyIndex(key)
                    )
                }
                    .flatMap {
                        rxErrorRepository.signTransaction(
                            it,
                            apiGetChallenge.transaction!!
                        )
                    }
            }
            .flatMap { vaultAuthApi.submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                if(prefsUtil.publicKey == key) prefsUtil.authToken = it.token
                localDataRepository.saveAuthToken(key!!, it.token!!)
                throw UserNotAuthorizedException("User Not Authorized")
            }
            .toObservable()
            .onErrorResumeNext {
                handleObservableRequestHttpError(
                    it,
                    Constant.ApiRequestTag.REFRESH_AUTH
                )
            }
            .map { throw DefaultException("") }
    }

    override fun <T> handleSingleRequestHttpError(
        throwable: Throwable,
        tag: Int,
        publicKey: String?
    ): Single<T> {
        val ex = exceptionMapper.transformHttpToDomain(throwable)

        if (ex is ExpiredSignatureException) {
            return refreshSingleAuthentication(publicKey)
        }

        // Token renewal is handled in TokenExpiryAuthenticator. Here we can just logout user.
        return if (ex is UserNotAuthorizedException) {
            when (tag) {
                Constant.ApiRequestTag.REFRESH_AUTH -> Single.error(ex)
                else -> refreshSingleAuthentication(publicKey)
            }
        } else {
            Single.error(ex)
        }
    }

    private fun <T> refreshSingleAuthentication(publicKey: String?): Single<T> {
        // Throw UserNotAuthorizedException with AUTH_REQUIRED action for non mnemonics case.
        if (prefsUtil.encryptedPhrases.isNullOrEmpty()) {
            return Single.error(UserNotAuthorizedException("User Not Authorized", AUTH_REQUIRED))
        }

        val key = publicKey ?: prefsUtil.publicKey
        return vaultAuthApi.getChallenge(key)
            .flatMap { apiGetChallenge ->
                getPhrases().flatMap {
                    rxErrorRepository.createKeyPair(
                        it.toCharArray(),
                        prefsUtil.getPublicKeyIndex(key)
                    )
                }
                    .flatMap {
                        rxErrorRepository.signTransaction(
                            it,
                            apiGetChallenge.transaction!!
                        )
                    }
            }
            .flatMap { vaultAuthApi.submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                if(prefsUtil.publicKey == key) prefsUtil.authToken = it.token
                localDataRepository.saveAuthToken(key!!, it.token!!)
                throw UserNotAuthorizedException("User Not Authorized")
            }
            .onErrorResumeNext {
                handleSingleRequestHttpError(
                    it,
                    Constant.ApiRequestTag.REFRESH_AUTH
                )
            }
            .map { throw DefaultException("") }
    }

    private fun getPhrases(): Single<String> {
        return Single.fromCallable {
            return@fromCallable keyStoreRepository.decryptData(
                PrefsUtil.PREF_ENCRYPTED_PHRASES,
                PrefsUtil.PREF_PHRASES_IV
            )
        }
    }

    override fun handleCompletableRequestHttpError(
        throwable: Throwable,
        tag: Int,
        publicKey: String?
    ): Completable {
        val ex = exceptionMapper.transformHttpToDomain(throwable)

        if (ex is ExpiredSignatureException) {
            return refreshCompletableAuthentication(publicKey)
        }

        // Token renewal is handled in TokenExpiryAuthenticator. Here we can just logout user.
        return if (ex is UserNotAuthorizedException) {
            when (tag) {
                Constant.ApiRequestTag.REFRESH_AUTH -> Completable.error(ex)
                else -> refreshCompletableAuthentication(publicKey)
            }
        } else {
            Completable.error(ex)
        }
    }

    private fun refreshCompletableAuthentication(publicKey: String?): Completable {
        // Throw UserNotAuthorizedException with AUTH_REQUIRED action for non mnemonics case.
        if (prefsUtil.encryptedPhrases.isNullOrEmpty()) {
            return Completable.error(
                UserNotAuthorizedException(
                    "User Not Authorized",
                    AUTH_REQUIRED
                )
            )
        }

        val key = publicKey ?: prefsUtil.publicKey
        return vaultAuthApi.getChallenge(key)
            .flatMap { apiGetChallenge ->
                getPhrases().flatMap {
                    rxErrorRepository.createKeyPair(
                        it.toCharArray(),
                        prefsUtil.getPublicKeyIndex(key)
                    )
                }
                    .flatMap {
                        rxErrorRepository.signTransaction(
                            it,
                            apiGetChallenge.transaction!!
                        )
                    }
            }
            .flatMap { vaultAuthApi.submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                if(prefsUtil.publicKey == key) prefsUtil.authToken = it.token
                localDataRepository.saveAuthToken(key!!, it.token!!)
                throw UserNotAuthorizedException("User Not Authorized")
            }
            .ignoreElement()
            .onErrorResumeNext {
                handleCompletableRequestHttpError(
                    it,
                    Constant.ApiRequestTag.REFRESH_AUTH
                )
            }
    }
}