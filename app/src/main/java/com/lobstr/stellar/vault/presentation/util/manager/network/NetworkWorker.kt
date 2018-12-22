package com.lobstr.stellar.vault.presentation.util.manager.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.presentation.application.LVApplication
import javax.inject.Inject


class NetworkWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    init {
        LVApplication.sAppComponent.inject(this)
    }

    @Inject
    lateinit var mEventProviderModule: EventProviderModule

    override fun doWork(): Result {
        mEventProviderModule.networkEventSubject.onNext(Network(Network.Type.CONNECTED))
        return Result.success()
    }
}