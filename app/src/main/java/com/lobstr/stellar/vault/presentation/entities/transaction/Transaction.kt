package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    val operations: List<Operation>,
    var mSequenceNumber: Long = 0
) : Parcelable