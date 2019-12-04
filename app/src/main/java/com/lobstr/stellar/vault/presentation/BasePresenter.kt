package com.lobstr.stellar.vault.presentation

import com.lobstr.stellar.vault.presentation.util.manager.network.NetworkWorker
import com.lobstr.stellar.vault.presentation.util.manager.network.WorkerManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView
import java.util.*


open class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    private val compositeDisposable = CompositeDisposable()
    protected var networkWorkerId: UUID? = null
    protected var needCheckConnectionState = false

    protected fun unsubscribeOnDestroy(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    /**
     * This method must be used with caution - it unsubscribe all Disposable elements
     * (including Internet Connection handler)
     */
    protected fun unsubscribeNow() {
        compositeDisposable.clear()
    }

    /**
     * Call it after internet connection fail.
     * @param checkConnectionState Notify [needCheckConnectionState] flag about changes if needed. By default true. null - ignore it.
     */
    protected fun handleNoInternetConnection(checkConnectionState: Boolean? = true) {
        // Prevent creation several NetworkWorkers.
        if (networkWorkerId != null) {
            return
        }

        needCheckConnectionState = checkConnectionState ?: needCheckConnectionState
        networkWorkerId = WorkerManager.createNetworkStateWorker(NetworkWorker::class.java)
    }

    /**
     * Cancel Network Worker.
     * @param checkConnectionState Notify [needCheckConnectionState] flag about changes if needed. By default null - ignore it.
     */
    protected fun cancelNetworkWorker(checkConnectionState: Boolean? = null) {
        needCheckConnectionState = checkConnectionState ?: needCheckConnectionState
        WorkerManager.cancelWorkById(networkWorkerId)
        networkWorkerId = null
    }

    /**
     * Recheck connection receiver.
     */
    override fun attachView(view: View?) {
        if (needCheckConnectionState)
            handleNoInternetConnection()
        super.attachView(view)
    }

    /**
     * Detach connection receiver.
     */
    override fun detachView(view: View) {
        cancelNetworkWorker()
        super.detachView(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeNow()
    }
}