package com.lobstr.stellar.vault.domain.operation_details

import com.lobstr.stellar.vault.domain.account.AccountRepository
import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.rxjava3.core.Single


class OperationDetailsInteractorImpl(
    private val accountRepository: AccountRepository
) : OperationDetailsInteractor {

    override fun getStellarAccount(stellarAddress: String): Single<Account> {
        return accountRepository.getStellarAccount(stellarAddress, "id")
    }

    override fun getAccountNames(): Map<String, String?> {
        return accountRepository.getAccountNames()
    }
}
