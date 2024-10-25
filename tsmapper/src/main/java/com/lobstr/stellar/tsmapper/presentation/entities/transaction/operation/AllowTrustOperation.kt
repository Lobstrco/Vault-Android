package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllowTrustOperation(
    override val sourceAccount: String?,
    val trustor: String,
    val assetCode: String,
    val authorize: Int
) : Operation(sourceAccount), Parcelable {

    // Contained TrustLineFlags from Stellar SDK.
    companion object {
        const val UNAUTHORIZED_FLAG = 0
        const val AUTHORIZED_FLAG = 1
        const val AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG = 2
    }

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (trustor.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_trustor), trustor, trustor))
        if (assetCode.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_asset_code), assetCode))
        fields.add(OperationField(context.getString(R.string.op_field_authorize), mapFlagToString(context, authorize)))

        return fields
    }

    private fun mapFlagToString(context: Context, flag: Int): String {
        return when(flag) {
            UNAUTHORIZED_FLAG -> context.getString(R.string.op_value_unauthorized_flag)
            AUTHORIZED_FLAG -> context.getString(R.string.op_value_authorized_flag)
            AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG -> context.getString(R.string.op_value_authorized_to_maintain_liabilities_flag)
            else -> "Unknown"
        }
    }
}