package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.Single

interface FcmRepository {

    fun fcmDeviceRegistration(token: String, type: String, registrationId: String): Single<FcmResult>

    fun transformApiAccountResponse(apiAccountStr: String): Account

    fun transformApiTransactionResponse(apiTransactionItemStr: String): TransactionItem
}