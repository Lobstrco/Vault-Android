package com.lobstr.stellar.vault.domain.operation_details

import com.lobstr.stellar.vault.presentation.entities.account.Account
import io.reactivex.rxjava3.core.Single


interface OperationDetailsInteractor {
    fun getStellarAccount(stellarAddress: String): Single<Account>
}