package com.lobstr.stellar.vault.data.fcm

import com.lobstr.stellar.vault.data.net.entities.fcm.ApiFcmResult
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult


class FcmEntityMapper {
    public fun transformFcmResponse(apiFcmResult: ApiFcmResult): FcmResult {
        // Get FCM info
        val id = apiFcmResult.id
        val name = apiFcmResult.name
        val token = apiFcmResult.token
        val deviceId = apiFcmResult.deviceId
        val isActive = apiFcmResult.isActive
        val dateCreated = apiFcmResult.dateCreated
        val type = apiFcmResult.type

        return FcmResult(
            id,
            name,
            token,
            deviceId,
            isActive,
            dateCreated,
            type
        )
    }
}