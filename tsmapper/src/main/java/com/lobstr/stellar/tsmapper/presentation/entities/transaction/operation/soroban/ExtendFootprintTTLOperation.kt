package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize
@Parcelize
data class ExtendFootprintTTLOperation(
    override val sourceAccount: String?,
    val extendTo: Long
) : Operation(sourceAccount), Parcelable {
    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_extend_to), extendTo.let {
            "${amountFormatter(it.toString())} ${context.resources.getQuantityString(R.plurals.op_value_ledger, it.toInt())}"
        }))

        return fields
    }
}