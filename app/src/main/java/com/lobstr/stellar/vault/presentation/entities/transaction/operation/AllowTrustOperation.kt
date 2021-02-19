package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.AppUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllowTrustOperation(
    override val sourceAccount: String?,
    val trustor: String,
    val assetCode: String,
    val authorize: Boolean
) : Operation(sourceAccount), Parcelable {

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (trustor.isNotEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_trustor), trustor))
        if (assetCode.isNotEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_code), assetCode))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_authorize), authorize.toString()))

        return fields
    }
}