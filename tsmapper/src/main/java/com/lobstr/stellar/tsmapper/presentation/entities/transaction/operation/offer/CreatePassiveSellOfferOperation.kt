package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CreatePassiveSellOfferOperation(
    override val sourceAccount: String?,
    val selling: Asset.CanonicalAsset,
    val buying: Asset.CanonicalAsset,
    val amount: String,
    val price: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_selling), selling.assetCode, selling))
        if (selling.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), selling.assetIssuer, selling.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_buying), buying.assetCode, buying))
        if (buying.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), buying.assetIssuer, buying.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_amount), amountFormatter(amount)))
        fields.add(OperationField(context.getString(R.string.op_field_price), amountFormatter(price)))
        fields.add(OperationField(context.getString(R.string.op_field_total), amountFormatter(BigDecimal(amount).multiply(BigDecimal(price)).stripTrailingZeros().toPlainString())))

        return fields
    }
}