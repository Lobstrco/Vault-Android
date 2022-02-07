package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllowTrustOperation(
    override val sourceAccount: String?,
    val trustor: String,
    val assetCode: String,
    val authorize: Boolean
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (trustor.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_trustor), trustor, trustor))
        if (assetCode.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_asset_code), assetCode))
        fields.add(OperationField(context.getString(R.string.op_field_authorize), authorize.toString()))

        return fields
    }
}