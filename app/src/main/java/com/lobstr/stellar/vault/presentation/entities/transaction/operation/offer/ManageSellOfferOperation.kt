package com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ManageSellOfferOperation(
    override val sourceAccount: String?,
    private val selling: Asset,
    private val buying: Asset,
    private val price: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(AppUtil.getString(R.string.op_field_selling), selling.assetCode))
        if (selling.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), selling.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_buying), buying.assetCode))
        if (buying.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), buying.assetIssuer))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_price), price))

        return fields
    }
}