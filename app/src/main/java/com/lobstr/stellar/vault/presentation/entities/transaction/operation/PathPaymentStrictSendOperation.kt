package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class PathPaymentStrictSendOperation(
    override val sourceAccount: String?,
    val sendAsset: Asset,
    val sendAmount: String,
    val destination: String,
    val destAsset: Asset,
    val destMin: String,
    val path: List<Asset>?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_send_asset), sendAsset.assetCode, sendAsset))
        if (sendAsset.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), sendAsset.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_send_amount), sendAmount))
        if (destination.isNotEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_destination), destination))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_dest_asset), destAsset.assetCode, destAsset))
        if (destAsset.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), destAsset.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_dest_min), destMin))

        if (!path.isNullOrEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_path), path.joinToString(separator = " ") { it.assetCode }))

        return fields
    }
}