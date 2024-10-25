package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class RestoreFootprintOperation(
    override val sourceAccount: String?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        return super.getFields(context, amountFormatter)
    }
}