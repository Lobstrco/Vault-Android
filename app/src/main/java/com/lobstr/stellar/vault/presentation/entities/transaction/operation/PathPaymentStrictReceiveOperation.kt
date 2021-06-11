package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class PathPaymentStrictReceiveOperation(
    override val sourceAccount: String?,
    val sendAsset: Asset,
    val sendMax: String,
    val destination: String,
    val destAsset: Asset,
    val destAmount: String,
    val path: List<Asset>?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_send_asset), sendAsset.assetCode, sendAsset))
        if (sendAsset.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), sendAsset.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_send_max), sendMax))
        if (destination.isNotEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_destination), destination))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_dest_asset), destAsset.assetCode, destAsset))
        if (destAsset.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), destAsset.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_filed_dest_amount), destAmount))

        if (!path.isNullOrEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_path), path.joinToString(separator = " ") { it.assetCode }, if(path.size == 1) path.first() else null))

        return fields
    }
}