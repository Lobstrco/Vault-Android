package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize


@Parcelize
data class CancelBuyOfferOperation(
    override val sourceAccount: String?,
    private val selling: Asset.CanonicalAsset,
    private val buying: Asset.CanonicalAsset,
    private val amount: String,
    private val price: String,
    private val offerId: Long
) : ManageBuyOfferOperation(sourceAccount, selling, buying, amount, price, offerId), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_offer_id), offerId.toString()))
        fields.add(OperationField(context.getString(R.string.op_field_selling), selling.assetCode, selling))
        if (selling.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), selling.assetIssuer, selling.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_buying), buying.assetCode, buying))
        if (buying.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), buying.assetIssuer, buying.assetIssuer))
        fields.add(OperationField(context.getString(R.string.op_field_price), price))

        return fields
    }
}