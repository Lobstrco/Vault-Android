package com.lobstr.stellar.vault.data.fcm

import com.lobstr.stellar.vault.data.net.FcmApi
import com.lobstr.stellar.vault.data.net.entities.fcm.ApiFcmResult
import com.lobstr.stellar.vault.domain.fcm.FcmRepository
import com.lobstr.stellar.vault.presentation.fcm.entities.FcmResult
import io.reactivex.Single


class FcmRepositoryImpl(private val fcmApi: FcmApi, private val fcmEntityMapper: FcmEntityMapper) :
    FcmRepository {
    override fun fcmDeviceRegistration(token: String, type: String, registrationId: String): Single<FcmResult> {
        return fcmApi.fcmDeviceRegistration(token, type, registrationId)
            .map { response ->
                val fcmResult: ApiFcmResult = response.body()!!
                fcmEntityMapper.transformFcmResponse(fcmResult)
            }
    }
}