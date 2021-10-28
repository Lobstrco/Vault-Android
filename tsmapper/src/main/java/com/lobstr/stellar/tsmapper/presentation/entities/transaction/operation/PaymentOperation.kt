package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentOperation(
    override val sourceAccount: String?,
    val destination: String,
    val asset: Asset,
    val amount: String
) : Operation(sourceAccount) {

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (destination.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_destination), destination, destination))
        fields.add(OperationField(context.getString(R.string.op_field_asset), asset.assetCode, asset))
        fields.add(OperationField(context.getString(R.string.op_field_amount), amount))
        if (asset.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), asset.assetIssuer, asset.assetIssuer))

        return fields
    }
}