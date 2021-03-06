package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionItem(
    val cancelledAt: String?,
    val addedAt: String?,
    val xdr: String?,
    val signedAt: String?,
    val hash: String,
    val getStatusDisplay: String?,
    var status: Int?,
    val sequenceOutdatedAt: String?,
    val transactionType: String?,
    val transaction: Transaction,
    var federation: String? = null,
    var name: String? = null
) : Parcelable