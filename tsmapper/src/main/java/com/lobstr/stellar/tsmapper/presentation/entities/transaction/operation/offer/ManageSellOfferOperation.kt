package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
open class ManageSellOfferOperation(
    override val sourceAccount: String?,
    private val selling: Asset,
    private val buying: Asset,
    private val amount: String,
    private val price: String,
    private val offerId: Long
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_selling), selling.assetCode, selling))
        if (selling.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), selling.assetIssuer, selling.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_buying), buying.assetCode, buying))
        if (buying.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), buying.assetIssuer, buying.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_amount), amount))
        fields.add(OperationField(context.getString(R.string.op_field_price), price))
        if(offerId != 0L) fields.add(OperationField(context.getString(R.string.op_field_offer_id), offerId.toString()))

        return fields
    }
}