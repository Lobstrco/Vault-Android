package com.lobstr.stellar.vault.domain.account

import io.reactivex.Completable


interface AccountRepository {
    fun updateAccountSigners(token: String, account: String): Completable
}