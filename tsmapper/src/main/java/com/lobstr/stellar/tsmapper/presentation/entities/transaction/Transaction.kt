package com.lobstr.stellar.tsmapper.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val hash: String,
    val envelopXdr: String,
    val fee: Long,
    val sourceAccount: String,
    val memo: TsMemo,
    val operations: List<Operation>,
    var sequenceNumber: Long = 0,
    var transactionType: String? = null
) : Parcelable