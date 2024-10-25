package com.lobstr.stellar.tsmapper.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.ExtendFootprintTTLOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.InvokeHostFunctionOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.RestoreFootprintOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.data.SorobanData
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val hash: String,
    val envelopXdr: String,
    val fee: Long,
    val sourceAccount: String,
    val memo: TsMemo,
    val timeBounds: TsTimeBounds,
    val operations: List<Operation>,
    var sequenceNumber: Long = 0,
    var transactionType: String? = null,
    val sorobanData: SorobanData? = null
) : Parcelable {

    /**
     * @return true if this transaction is a Soroban transaction.
     */
    fun isSorobanTransaction(): Boolean = if (operations.size != 1) {
        false
    } else {
        when (operations[0]) {
            is InvokeHostFunctionOperation,
            is ExtendFootprintTTLOperation,
            is RestoreFootprintOperation -> true

            else -> false
        }
    }
}