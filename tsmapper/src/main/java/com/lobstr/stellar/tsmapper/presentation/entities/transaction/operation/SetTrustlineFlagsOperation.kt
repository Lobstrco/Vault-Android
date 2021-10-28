package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import kotlinx.parcelize.Parcelize

@Parcelize
data class SetTrustlineFlagsOperation(
    override val sourceAccount: String?,
    val trustor: String,
    val asset: Asset,
    val clearFlags: List<Int>?,
    val setFlags: List<Int>?
) : Operation(sourceAccount), Parcelable {

    // Contained TrustLineFlags from Stellar SDK.
    companion object {
        const val AUTHORIZED_FLAG = 1
        const val AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG = 2
        const val TRUSTLINE_CLAWBACK_ENABLED_FLAG = 4
    }

    override fun getFields(context: Context): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (trustor.isNotEmpty()) fields.add(OperationField(context.getString(R.string.op_field_trustor), trustor, trustor))
        fields.add(OperationField(context.getString(R.string.op_field_asset), asset.assetCode, asset))
        if (asset.assetIssuer != null) fields.add(OperationField(context.getString(R.string.op_field_asset_issuer), asset.assetIssuer, asset.assetIssuer))

        if(!clearFlags.isNullOrEmpty()) {
            fields.add(
                OperationField(
                    context.getString(R.string.op_field_clear_flags),
                clearFlags.joinToString(separator = " ") {
                    return@joinToString mapFlagToString(context, it).plus("\n")
                }.trim()
            )
            )
        }

        if(!setFlags.isNullOrEmpty()) {
            fields.add(
                OperationField(
                    context.getString(R.string.op_field_set_flags),
                setFlags.joinToString(separator = " ") {
                    return@joinToString mapFlagToString(context,it).plus("\n")
                }.trim()
            )
            )
        }

        return fields
    }

    private fun mapFlagToString(context: Context, flag: Int): String {
        return when(flag) {
            AUTHORIZED_FLAG -> context.getString(R.string.op_value_authorized_flag)
            AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG -> context.getString(R.string.op_value_authorized_to_maintain_liabilities_flag)
            TRUSTLINE_CLAWBACK_ENABLED_FLAG -> context.getString(R.string.op_value_trustline_clawback_enabled_flag)
            else -> "Unknown"
        }
    }
}