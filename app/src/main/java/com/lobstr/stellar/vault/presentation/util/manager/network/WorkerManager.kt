package com.lobstr.stellar.vault.presentation.util.manager.network

import androidx.work.*
import com.lobstr.stellar.vault.presentation.application.LVApplication
import java.util.*


object WorkerManager {

    fun createNetworkStateWorker(clazz: Class<out ListenableWorker>): UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val networkWorkRequest =
            OneTimeWorkRequest.Builder(clazz).setConstraints(constraints)
                .build()
        WorkManager.getInstance(LVApplication.appComponent.context).enqueue(networkWorkRequest)
        return networkWorkRequest.id
    }

    fun cancelWorkById(id: UUID?) {
        if (id != null)
            WorkManager.getInstance(LVApplication.appComponent.context).cancelWorkById(id)
    }
}