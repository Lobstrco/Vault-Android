package com.lobstr.stellar.vault.domain.error

import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface RxErrorUtils {

    fun <T : Any> handleObservableRequestHttpError(
        throwable: Throwable,
        tag: Int = Constant.ApiRequestTag.DEFAULT,
        publicKey: String? = null
    ): Observable<T>

    fun <T : Any> handleSingleRequestHttpError(
        throwable: Throwable,
        tag: Int = Constant.ApiRequestTag.DEFAULT,
        publicKey: String? = null
    ): Single<T>

    fun handleCompletableRequestHttpError(
        throwable: Throwable,
        tag: Int = Constant.ApiRequestTag.DEFAULT,
        publicKey: String? = null
    ): Completable
}