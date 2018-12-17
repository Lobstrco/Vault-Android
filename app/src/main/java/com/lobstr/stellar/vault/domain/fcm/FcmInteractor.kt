package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.fcm.entities.FcmResult
import io.reactivex.Single


interface FcmInteractor {

    fun fcmDeviceRegistration(type: String, registrationId: String): Single<FcmResult>

    fun saveFcmToken(token: String)

    fun getFcmToken(): String?

    fun saveAppVersion(appVersion: Int)

    fun getAppVersion(): Int

    fun setFcmRegistered(registered: Boolean)

    fun isFcmRegistered(): Boolean
}