package com.lobstr.stellar.vault.presentation

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    private val mCompositeDisposable = CompositeDisposable()

    protected fun unsubscribeOnDestroy(disposable: Disposable?) {
        if (disposable == null) {
            return
        }

        mCompositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}