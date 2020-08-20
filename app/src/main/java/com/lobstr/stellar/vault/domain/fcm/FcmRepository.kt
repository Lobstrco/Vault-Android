package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.rxjava3.core.Single

interface FcmRepository {

    fun fcmDeviceRegistration(
        token: String,
        type: String,
        registrationId: String,
        active: Boolean
    ): Single<FcmResult>

    fun transformApiAccountResponse(apiAccountStr: String): Account

    fun transformApiTransactionResponse(apiTransactionItemStr: String): TransactionItem
}