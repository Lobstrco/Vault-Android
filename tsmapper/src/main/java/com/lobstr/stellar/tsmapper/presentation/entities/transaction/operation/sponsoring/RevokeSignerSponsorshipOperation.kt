package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class RevokeSignerSponsorshipOperation(
    override val sourceAccount: String?,
    val accountId: String,
    val signerPublicKey: String?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_account_id), accountId, accountId))
        signerPublicKey?.let {
            fields.add(OperationField(context.getString(R.string.op_field_signer_public_key), it, it))
        }

        return fields
    }
}