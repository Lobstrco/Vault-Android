package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.Single

class FcmInteractorImpl(private val fcmRepository: FcmRepository, private val prefsUtil: PrefsUtil) : FcmInteractor {

    override fun fcmDeviceRegistration(type: String, registrationId: String): Single<FcmResult> {
        return fcmRepository.fcmDeviceRegistration(
            AppUtil.getJwtToken(prefsUtil.authToken),
            type,
            registrationId
        )
    }

    override fun saveFcmToken(token: String) {
        prefsUtil.fcmToken = token
    }

    override fun getFcmToken(): String? {
        return prefsUtil.fcmToken
    }

    override fun saveAppVersion(appVersion: Int) {
        prefsUtil.appVersion = appVersion

    }

    override fun getAppVersion(): Int {
        return prefsUtil.appVersion
    }

    override fun setFcmRegistered(registered: Boolean) {
        prefsUtil.isFcmRegisteredSuccessfully = registered
    }

    override fun isFcmRegistered(): Boolean {
        return prefsUtil.isFcmRegisteredSuccessfully
    }

    override fun confirmIsUserSignerForLobstr(jsonStr: String?): Account? {
        prefsUtil.accountHasSigners = true

        if (jsonStr.isNullOrEmpty()) {
            return null
        }

        val account = fcmRepository.transformApiAccountResponse(jsonStr)

        // increment signers count
        prefsUtil.accountSignersCount = ++prefsUtil.accountSignersCount

        return account
    }

    override fun transformNewTransactionResponse(jsonStr: String?): TransactionItem? {
        if (jsonStr.isNullOrEmpty()) {
            return null
        }

        return fcmRepository.transformApiTransactionResponse(jsonStr)
    }

    override fun isUserAuthorized(): Boolean {
        return !prefsUtil.authToken.isNullOrEmpty()
    }
}