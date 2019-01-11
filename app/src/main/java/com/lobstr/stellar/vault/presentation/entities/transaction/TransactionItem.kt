package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionItem(
    val cancelledAt: String?,
    val addedAt: String?,
    val xdr: String?,
    val signedAt: String?,
    val hash: String,
    val getStatusDisplay: String?,
    val status: Int?,
    val transaction: Transaction
) : Parcelable