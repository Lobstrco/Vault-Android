package com.lobstr.stellar.vault.data.account

import com.lobstr.stellar.vault.data.net.AccountApi
import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.domain.error.RxErrorUtils
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountConfig
import com.lobstr.stellar.vault.presentation.entities.account.AppVersion
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil
import com.lobstr.stellar.vault.presentation.util.FileStreamUtil.Companion.ACCOUNT_NAME_STORAGE
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


class AccountRepositoryImpl(
    private val accountApi: AccountApi, private val accountEntityMapper: AccountEntityMapper,
    private val rxErrorUtils: RxErrorUtils, private val file: FileStreamUtil
) :
    AccountRepository {

    override fun updateAccountSigners(token: String, account: String): Completable {
        return accountApi.updateAccountSigners(token, account)
            .onErrorResumeNext { rxErrorUtils.handleCompletableRequestHttpError(it) }
    }

    override fun getSignedAccounts(token: String): Single<List<Account>> {
        return accountApi.getSignedAccounts(token)
            .map { accountEntityMapper.transformSignedAccounts(it) }
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
    }

    override fun getStellarAccount(accountId: String, type: String): Single<Account> {
        return accountApi.getStellarAccount(accountId, type)
            .map { accountEntityMapper.transformStellarAccount(it) }
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
    }

    override fun getAccountConfig(token: String): Single<AccountConfig> {
        return accountApi.getAccountConfig(token)
            .map { accountEntityMapper.transformAccountConfig(it) }
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
    }

    override fun updateAccountConfig(
        token: String,
        spamProtectionEnabled: Boolean
    ): Single<AccountConfig> {
        return accountApi.updateAccountConfig(token, spamProtectionEnabled)
            .map { accountEntityMapper.transformAccountConfig(it) }
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
    }

    override fun getAppVersion(): Single<AppVersion> {
        return accountApi.getAppVersion()
            .map { accountEntityMapper.transformAppVersion(it) }
            .onErrorResumeNext { rxErrorUtils.handleSingleRequestHttpError(it) }
    }

    override fun getAccountNames(): MutableMap<String, String?> {
        return file.read<MutableMap<String, String?>>(ACCOUNT_NAME_STORAGE)
            ?: mutableMapOf()
    }

    override fun saveAccountName(publicKey: String, name: String?) {
        file.write(ACCOUNT_NAME_STORAGE, getAccountNames().apply {
            if (name.isNullOrEmpty()) {
                remove(publicKey)
            } else {
                put(publicKey, name)
            }
        })
    }

    override fun clearAccountNames() {
        file.deleteFile(ACCOUNT_NAME_STORAGE)
    }
}