package com.lobstr.stellar.vault.data.error

import com.lobstr.stellar.vault.data.error.exeption.DefaultException
import com.lobstr.stellar.vault.data.error.exeption.ExpiredSignatureException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException
import com.lobstr.stellar.vault.data.error.exeption.UserNotAuthorizedException.Action.AUTH_REQUIRED
import com.lobstr.stellar.vault.data.net.VaultAuthApi
import com.lobstr.stellar.vault.domain.error.RxErrorRepository
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.key_store.KeyStoreRepository
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
    private val prefsUtil: PrefsUtil
) : RxErrorUtils {

    override fun <T> handleObservableRequestHttpError(throwable: Throwable, tag: Int): Observable<T> {
        val ex = exceptionMapper.transformHttpToDomain(throwable)

        if (ex is ExpiredSignatureException) {
            return refreshObservableAuthentication()
        }

        // Token renewal is handled in TokenExpiryAuthenticator. Here we can just logout user.
        return if (ex is UserNotAuthorizedException) {
            when (tag) {
                Constant.ApiRequestTag.REFRESH_AUTH -> Observable.error(ex)
                else -> refreshObservableAuthentication()
            }
        } else {
            Observable.error(ex)
        }
    }

    private fun <T> refreshObservableAuthentication(): Observable<T> {
        // Throw UserNotAuthorizedException with AUTH_REQUIRED action for non mnemonics case.
        if(prefsUtil.encryptedPhrases.isNullOrEmpty()){
            return Observable.error(UserNotAuthorizedException("User Not Authorized", AUTH_REQUIRED))
        }
        return vaultAuthApi.getChallenge(prefsUtil.publicKey)
            .flatMap { apiGetChallenge ->
                getPhrases().flatMap { rxErrorRepository.createKeyPair(it.toCharArray(), 0) }
                    .flatMap { rxErrorRepository.signTransaction(it, apiGetChallenge.transaction!!) }
            }
            .flatMap { vaultAuthApi.submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                prefsUtil.authToken = it.token
                throw UserNotAuthorizedException("User Not Authorized")
            }
            .toObservable()
            .onErrorResumeNext { it: Throwable ->
                handleObservableRequestHttpError(
                    it,
                    Constant.ApiRequestTag.REFRESH_AUTH
                )
            }
            .map { throw DefaultException("") }
    }

    override fun <T> handleSingleRequestHttpError(throwable: Throwable, tag: Int): Single<T> {
        val ex = exceptionMapper.transformHttpToDomain(throwable)

        if (ex is ExpiredSignatureException) {
            return refreshSingleAuthentication()
        }

        // Token renewal is handled in TokenExpiryAuthenticator. Here we can just logout user.
        return if (ex is UserNotAuthorizedException) {
            when (tag) {
                Constant.ApiRequestTag.REFRESH_AUTH -> Single.error(ex)
                else -> refreshSingleAuthentication()
            }
        } else {
            Single.error(ex)
        }
    }

    private fun <T> refreshSingleAuthentication(): Single<T> {
        // Throw UserNotAuthorizedException with AUTH_REQUIRED action for non mnemonics case.
        if(prefsUtil.encryptedPhrases.isNullOrEmpty()){
            return Single.error(UserNotAuthorizedException("User Not Authorized", AUTH_REQUIRED))
        }
        return vaultAuthApi.getChallenge(prefsUtil.publicKey)
            .flatMap { apiGetChallenge ->
                getPhrases().flatMap { rxErrorRepository.createKeyPair(it.toCharArray(), 0) }
                    .flatMap { rxErrorRepository.signTransaction(it, apiGetChallenge.transaction!!) }
            }
            .flatMap { vaultAuthApi.submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                prefsUtil.authToken = it.token
                throw UserNotAuthorizedException("User Not Authorized")
            }
            .onErrorResumeNext { handleSingleRequestHttpError(it, Constant.ApiRequestTag.REFRESH_AUTH) }
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

    override fun handleCompletableRequestHttpError(throwable: Throwable, tag: Int): Completable {
        val ex = exceptionMapper.transformHttpToDomain(throwable)

        if (ex is ExpiredSignatureException) {
            return refreshCompletableAuthentication()
        }

        // Token renewal is handled in TokenExpiryAuthenticator. Here we can just logout user.
        return if (ex is UserNotAuthorizedException) {
            when (tag) {
                Constant.ApiRequestTag.REFRESH_AUTH -> Completable.error(ex)
                else -> refreshCompletableAuthentication()
            }
        } else {
            Completable.error(ex)
        }
    }

    private fun refreshCompletableAuthentication(): Completable {
        // Throw UserNotAuthorizedException with AUTH_REQUIRED action for non mnemonics case.
        if(prefsUtil.encryptedPhrases.isNullOrEmpty()){
            return Completable.error(UserNotAuthorizedException("User Not Authorized", AUTH_REQUIRED))
        }
        return vaultAuthApi.getChallenge(prefsUtil.publicKey)
            .flatMap { apiGetChallenge ->
                getPhrases().flatMap { rxErrorRepository.createKeyPair(it.toCharArray(), 0) }
                    .flatMap { rxErrorRepository.signTransaction(it, apiGetChallenge.transaction!!) }
            }
            .flatMap { vaultAuthApi.submitChallenge(it.toEnvelopeXdrBase64()) }
            .doOnSuccess {
                prefsUtil.authToken = it.token
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