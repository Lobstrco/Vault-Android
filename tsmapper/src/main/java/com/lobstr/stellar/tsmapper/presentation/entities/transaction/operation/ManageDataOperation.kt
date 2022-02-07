package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class ManageDataOperation(
    override val sourceAccount: String?,
    val name: String,
    val value: ByteArray?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_filed_name), name))
        value?.let {
            fields.add(OperationField(context.getString(R.string.op_field_value), String(it)))
        }

        return fields
    }
}