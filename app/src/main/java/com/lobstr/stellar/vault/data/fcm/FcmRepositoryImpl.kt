package com.lobstr.stellar.vault.data.fcm

import com.lobstr.stellar.vault.data.account.AccountEntityMapper
import com.lobstr.stellar.vault.data.net.FcmApi
import com.lobstr.stellar.vault.data.net.entities.account.ApiAccount
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.transaction.TransactionEntityMapper
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.domain.fcm.FcmRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.fcm.FcmResult
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.core.Single

class FcmRepositoryImpl(
    private val fcmApi: FcmApi,
    private val fcmEntityMapper: FcmEntityMapper,
    private val transactionEntityMapper: TransactionEntityMapper,
    private val accountEntityMapper: AccountEntityMapper,
    private val rxErrorUtils: RxErrorUtils
) : FcmRepository {

    override fun fcmDeviceRegistration(
        token: String,
        type: String,
        registrationId: String,
        active: Boolean,
        publicKey: String
    ): Single<FcmResult> = fcmApi.fcmDeviceRegistration(token, type, registrationId, active)
        .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it, publicKey = publicKey) }
        .map {
            fcmEntityMapper.transformFcmResponse(it)
        }

    override fun transformApiAccountResponse(apiAccountStr: String): Account =
        accountEntityMapper.transformAccount(
            AppUtil.convertJsonToPojo(
                apiAccountStr,
                ApiAccount::class.java
            )!!
        )

    override fun transformApiTransactionResponse(apiTransactionItemStr: String): TransactionItem =
        transactionEntityMapper.transformTransactionItem(
            AppUtil.convertJsonToPojo(
                apiTransactionItemStr,
                ApiTransactionItem::class.java
            )!!
        )
}