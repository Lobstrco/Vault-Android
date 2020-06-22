package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ManageDataOperation(
    override val sourceAccount: String?,
    val name: String,
    val value: ByteArray?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_filed_name), name))
        value?.let {
            fields.add(OperationField(AppUtil.getString(R.string.op_field_value), String(it)))
        }

        return fields
    }
}