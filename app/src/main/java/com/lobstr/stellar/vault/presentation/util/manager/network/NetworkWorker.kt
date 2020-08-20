package com.lobstr.stellar.vault.presentation.util.manager.network

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network


class NetworkWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val eventProviderModule: EventProviderModule
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        eventProviderModule.networkEventSubject.onNext(Network(Network.Type.CONNECTED, id))
        return Result.success()
    }
}