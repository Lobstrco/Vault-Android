package com.lobstr.stellar.vault.presentation.entities.transaction.operation.clawback

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClawbackOperation(
    override val sourceAccount: String?,
    val from: String,
    val asset: Asset,
    val amount: String
    ) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_from), from))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_asset), asset.assetCode, asset))
        if (asset.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), asset.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_amount), amount))

        return fields
    }
}