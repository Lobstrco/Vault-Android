package com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class RevokeDataSponsorshipOperation(
    override val sourceAccount: String?,
    val accountId: String,
    val dataName: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_account_id), accountId))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_data_name), dataName))

        return fields
    }
}