package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.Single

interface FcmInteractor {

    fun fcmDeviceRegistration(type: String, registrationId: String): Single<FcmResult>

    fun saveFcmToken(token: String)

    fun getFcmToken(): String?

    fun saveAppVersion(appVersion: Int)

    fun getAppVersion(): Int

    fun setFcmRegistered(registered: Boolean)

    fun isFcmRegistered(): Boolean

    fun confirmIsUserSignerForLobstr(jsonStr: String?): Account?

    fun isUserAuthorized(): Boolean

    fun transformNewTransactionResponse(jsonStr: String?): TransactionItem?
}