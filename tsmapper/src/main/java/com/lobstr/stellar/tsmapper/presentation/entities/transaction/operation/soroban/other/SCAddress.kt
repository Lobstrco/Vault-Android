package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SCAddress : Parcelable {
    data class Account(val accountId: String) : SCAddress()

    data class Contract(val contractId: String) : SCAddress()
}
