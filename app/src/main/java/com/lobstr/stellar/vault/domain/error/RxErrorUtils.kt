package com.lobstr.stellar.vault.domain.error

import com.lobstr.stellar.vault.presentation.util.Constant
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface RxErrorUtils {

    fun <T> handleObservableRequestHttpError(throwable: Throwable, tag: Int = Constant.ApiRequestTag.DEFAULT): Observable<T>

    fun <T> handleSingleRequestHttpError(throwable: Throwable, tag: Int = Constant.ApiRequestTag.DEFAULT): Single<T>

    fun handleCompletableRequestHttpError(throwable: Throwable, tag: Int = Constant.ApiRequestTag.DEFAULT): Completable
}