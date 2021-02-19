package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountMergeOperation(
    override val sourceAccount: String?,
    val destination: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (destination.isNotEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_destination), destination))

        return fields
    }
}