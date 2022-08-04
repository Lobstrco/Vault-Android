package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionItem(
    val addedAt: String?,
    val hash: String,
    var status: Int?,
    val sequenceOutdatedAt: String?,
    val transaction: Transaction,
    var federation: String? = null,
    var name: String? = null
) : Parcelable