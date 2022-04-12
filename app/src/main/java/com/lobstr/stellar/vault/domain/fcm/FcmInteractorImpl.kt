package com.lobstr.stellar.vault.domain.fcm

import com.lobstr.stellar.vault.domain.local_data.LocalDataRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.PrefsUtil
import io.reactivex.rxjava3.core.Single

class FcmInteractorImpl(
    private val fcmRepository: FcmRepository,
    private val localDataRepository: LocalDataRepository,
    private val prefsUtil: PrefsUtil
) : FcmInteractor {

    override fun fcmDeviceRegistrationByToken(
        type: String,
        registrationId: String,
        active: Boolean,
        authToken: String,
        publicKey: String
    ): Single<FcmResult> = fcmRepository.fcmDeviceRegistration(
        AppUtil.getJwtToken(authToken),
        type,
        registrationId,
        active,
        publicKey
    )

    override fun getAuthTokenList(isLogout: Boolean): List<Pair<String, String>> =
        mutableListOf<Pair<String, String>>().apply {
            val registeredMap = localDataRepository.getFcmRegisteredData()
            val tokenMap = localDataRepository.getAuthTokens()

            prefsUtil.getPublicKeyList().forEach {
                if (registeredMap[it] != true || isLogout) {
                    add(Pair(it, tokenMap[it] ?: ""))
                }
            }
        }

    override fun saveFcmToken(token: String) {
        prefsUtil.fcmToken = token
    }

    override fun getFcmToken(): String? = prefsUtil.fcmToken

    override fun getCurrentPublicKey(): String? = prefsUtil.publicKey

    override fun saveRegisteredFcmPublicKey(key: String, active: Boolean) {
        localDataRepository.saveIsFcmRegistered(key, active)
    }

    override fun setFcmNotRegistered() {
        prefsUtil.getPublicKeyList().forEach {
            localDataRepository.saveIsFcmRegistered(it, false)
        }
    }

    override fun isFcmRegistered(): Boolean {
        val registeredMap = localDataRepository.getFcmRegisteredData()
        prefsUtil.getPublicKeyList().forEach {
            if (registeredMap[it] != true) {
                return false
            }
        }

        return true
    }

    override fun userAccountReceived(jsonStr: String?): String? {
        if (jsonStr.isNullOrEmpty()) {
            return null
        }

        return fcmRepository.transformApiAccountResponse(jsonStr).address
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

    override fun isUserAuthorized(): Boolean = !prefsUtil.authToken.isNullOrEmpty()

    override fun isNotificationsEnabled(userAccount: String): Boolean =
        localDataRepository.getNotificationInfo(userAccount)
}