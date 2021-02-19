package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentOperation(
    override val sourceAccount: String?,
    val destination: String,
    val asset: Asset,
    val amount: String
) : Operation(sourceAccount) {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (destination.isNotEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_destination), destination))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_asset), asset.assetCode))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_amount), amount))
        if (asset.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), asset.assetIssuer))

        return fields
    }
}