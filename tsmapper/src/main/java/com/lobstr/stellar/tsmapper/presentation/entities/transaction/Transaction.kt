package com.lobstr.stellar.tsmapper.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val fee: Long,
    val envelopXdr: String,
    val sourceAccount: String,
    val memo: String?,
    val operations: List<Operation>,
    var mSequenceNumber: Long = 0,
    var transactionType: String? = null
) : Parcelable