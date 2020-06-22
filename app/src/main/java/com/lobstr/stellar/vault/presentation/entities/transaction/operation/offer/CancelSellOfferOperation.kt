package com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CancelSellOfferOperation(
    override val sourceAccount: String?,
    private val selling: Asset,
    private val buying: Asset,
    private val price: String,
    val offerId: Long
) : ManageSellOfferOperation(sourceAccount, selling, buying, price), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_selling), selling.assetCode))
        if (selling.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), selling.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_buying), buying.assetCode))
        if (buying.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), buying.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_price), price))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_offer_id), offerId.toString()))

        return fields
    }
}