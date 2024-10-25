package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.clawback

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClawbackOperation(
    override val sourceAccount: String?,
    val from: String,
    val asset: Asset.CanonicalAsset,
    val amount: String
    ) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_from), from, from))
        fields.add(OperationField(context.getString(R.string.op_field_asset), asset.assetCode, asset))
        if (asset.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), asset.assetIssuer, asset.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_amount), amountFormatter(amount)))

        return fields
    }
}