package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val sourceAccount: String,
    val memo: String?,
    val operations: List<Operation>,
    var mSequenceNumber: Long = 0,
    var federation: String? = null
) : Parcelable