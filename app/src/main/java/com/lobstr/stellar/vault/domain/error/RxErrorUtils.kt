package com.lobstr.stellar.vault.domain.error

import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface RxErrorUtils {

    fun <T> handleObservableRequestHttpError(throwable: Throwable, tag: Int = Constant.ApiRequestTag.DEFAULT): Observable<T>

    fun <T> handleSingleRequestHttpError(throwable: Throwable, tag: Int = Constant.ApiRequestTag.DEFAULT): Single<T>

    fun handleCompletableRequestHttpError(throwable: Throwable, tag: Int = Constant.ApiRequestTag.DEFAULT): Completable
}