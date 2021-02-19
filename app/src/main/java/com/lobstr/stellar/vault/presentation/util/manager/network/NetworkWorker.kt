package com.lobstr.stellar.vault.presentation.util.manager.network

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Network
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NetworkWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val eventProviderModule: EventProviderModule
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        eventProviderModule.networkEventSubject.onNext(Network(Network.Type.CONNECTED, id))
        return Result.success()
    }
}