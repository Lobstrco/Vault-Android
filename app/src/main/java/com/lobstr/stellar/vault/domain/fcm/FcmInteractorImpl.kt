package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class FcmInteractorImpl(private val fcmRepository: FcmRepository, private val prefsUtil: PrefsUtil) : FcmInteractor {

    override fun fcmDeviceRegistration(type: String, registrationId: String, active: Boolean): Single<FcmResult> {
        return fcmRepository.fcmDeviceRegistration(
            AppUtil.getJwtToken(prefsUtil.authToken),
            type,
            registrationId,
            active
        )
    }

    override fun saveFcmToken(token: String) {
        prefsUtil.fcmToken = token
    }

    override fun getFcmToken(): String? {
        return prefsUtil.fcmToken
    }

    override fun setFcmRegistered(registered: Boolean) {
        prefsUtil.isFcmRegisteredSuccessfully = registered
    }

    override fun isFcmRegistered(): Boolean {
        return prefsUtil.isFcmRegisteredSuccessfully
    }

    override fun signedNewAccount(jsonStr: String?): Account? {
        prefsUtil.accountHasSigners = true

        if (jsonStr.isNullOrEmpty()) {
            return null
        }

        val account = fcmRepository.transformApiAccountResponse(jsonStr)

        // Increment signers count.
        prefsUtil.accountSignersCount = ++prefsUtil.accountSignersCount

        return account
    }

    override fun removedSigner(jsonStr: String?): Account? {
        if (jsonStr.isNullOrEmpty()) {
            return null
        }

        val account = fcmRepository.transformApiAccountResponse(jsonStr)

        // Decrement signers count.
        if (prefsUtil.accountSignersCount > 0) {
            prefsUtil.accountSignersCount = --prefsUtil.accountSignersCount
        }

        return account
    }

    override fun transformTransactionResponse(jsonStr: String?): TransactionItem? {
        if (jsonStr.isNullOrEmpty()) {
            return null
        }

        return fcmRepository.transformApiTransactionResponse(jsonStr)
    }

    override fun isUserAuthorized(): Boolean {
        return !prefsUtil.authToken.isNullOrEmpty()
    }

    override fun isNotificationsEnabled(): Boolean {
        return prefsUtil.isNotificationsEnabled
    }
}