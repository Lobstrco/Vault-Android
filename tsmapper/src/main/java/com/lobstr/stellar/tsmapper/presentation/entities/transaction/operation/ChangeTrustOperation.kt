package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangeTrustOperation(
    override val sourceAccount: String?,
    val asset: Asset,
    val limit: String
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        mapAssetFields(context, fields, asset)
        fields.add(OperationField(context.getString(R.string.op_field_limit), limit))

        return fields
    }
}