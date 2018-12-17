package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.fcm.entities.FcmResult
import io.reactivex.Single


public interface FcmRepository {
    fun fcmDeviceRegistration(token: String, type: String, registrationId: String): Single<FcmResult>
}