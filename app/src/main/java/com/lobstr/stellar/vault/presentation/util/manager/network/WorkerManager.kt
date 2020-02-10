package com.lobstr.stellar.vault.presentation.util.manager.network

import androidx.work.*
import com.lobstr.stellar.vault.presentation.util.AppUtil
import java.util.*


object WorkerManager {

    fun createNetworkStateWorker(clazz: Class<out ListenableWorker>): UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val networkWorkRequest =
            OneTimeWorkRequest.Builder(clazz).setConstraints(constraints)
                .build()
        WorkManager.getInstance(AppUtil.getAppContext()).enqueue(networkWorkRequest)
        return networkWorkRequest.id
    }

    fun cancelWorkById(id: UUID?) {
        if (id != null)
            WorkManager.getInstance(AppUtil.getAppContext()).cancelWorkById(id)
    }
}