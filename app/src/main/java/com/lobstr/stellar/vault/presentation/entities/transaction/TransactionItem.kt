package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionItem(
    val cancelledAt: String?,
    val addedAt: String?,
    val signedAt: String?,
    val hash: String,
    val getStatusDisplay: String?,
    var status: Int?,
    val sequenceOutdatedAt: String?,
    val transaction: Transaction,
    var federation: String? = null,
    var name: String? = null
) : Parcelable