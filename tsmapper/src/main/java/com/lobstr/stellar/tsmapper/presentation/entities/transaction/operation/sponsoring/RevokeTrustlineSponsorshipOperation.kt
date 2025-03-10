package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import kotlinx.parcelize.Parcelize

@Parcelize
data class RevokeTrustlineSponsorshipOperation(
    override val sourceAccount: String?,
    val accountId: String,
    val asset: Asset.TrustLineAsset
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        fields.add(OperationField(context.getString(R.string.op_field_account_id), accountId, accountId))
        mapAssetFields(context, fields, asset, amountFormatter)

        return fields
    }
}