package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
data class PathPaymentStrictReceiveOperation(
    override val sourceAccount: String?,
    val sendAsset: Asset.CanonicalAsset,
    val sendMax: String,
    val destination: String,
    val destAsset: Asset.CanonicalAsset,
    val destAmount: String,
    val path: List<Asset.CanonicalAsset>?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (destination.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_destination), destination, destination))
        fields.add(OperationField(context.getString(R.string.op_field_send_asset), sendAsset.assetCode, sendAsset))
        if (sendAsset.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), sendAsset.assetIssuer, sendAsset.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_send_max), amountFormatter(sendMax)))
        fields.add(OperationField(context.getString(R.string.op_field_dest_asset), destAsset.assetCode, destAsset))
        if (destAsset.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), destAsset.assetIssuer, destAsset.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_filed_dest_amount), amountFormatter(destAmount)))

        if (!path.isNullOrEmpty()) fields.add(OperationField(context.getString(R.string.op_field_path), path.joinToString(separator = " ") { it.assetCode }, if(path.size == 1) path.first() else null))

        return fields
    }
}