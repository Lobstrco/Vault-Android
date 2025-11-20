package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import io.reactivex.rxjava3.core.Single

interface FcmInteractor {

    fun fcmDeviceRegistrationByToken(
        type: String,
        registrationId: String,
        active: Boolean,
        authToken: String,
        publicKey: String
    ): Single<FcmResult>

    fun getAuthTokenList(isLogout: Boolean = false): List<Pair<String, String>>

    fun saveFcmToken(token: String)

    fun getFcmToken(): String?

    fun getCurrentPublicKey(): String?

    fun saveRegisteredFcmPublicKey(key: String, active: Boolean)

    fun setFcmNotRegistered()

    fun isFcmRegistered(): Boolean

    fun userAccountReceived(jsonStr: String?): String?

    fun signedNewAccount(jsonStr: String?): Account?

    fun removedSigner(jsonStr: String?): Account?

    fun isUserAuthorized(): Boolean

    fun isNotificationsEnabled(userAccount: String): Boolean

    fun transformTransactionResponse(jsonStr: String?): TransactionItem?

    fun transformTransactionResponseToHash(jsonStr: String?): String?
}