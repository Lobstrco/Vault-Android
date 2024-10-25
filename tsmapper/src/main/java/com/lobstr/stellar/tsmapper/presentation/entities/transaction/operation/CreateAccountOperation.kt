package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAccountOperation(
    override val sourceAccount: String?,
    val destination: String,
    val asset: Asset.CanonicalAsset,
    val startingBalance: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (destination.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_destination), destination, destination))
        fields.add(OperationField(context.getString(R.string.op_field_asset), asset.assetCode, asset))
        if (startingBalance.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_starting_balance), amountFormatter(startingBalance)))

        return fields
    }
}