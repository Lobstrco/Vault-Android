package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.util.AppUtil
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

    override fun getFields(): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (trustor.isNotEmpty()) fields.add(OperationField(AppUtil.getString(R.string.op_field_trustor), trustor))
        fields.add(OperationField(AppUtil.getString(R.string.op_field_asset), asset.assetCode, asset))
        if (asset.assetIssuer != null) fields.add(OperationField(AppUtil.getString(R.string.op_field_asset_issuer), asset.assetIssuer))

        if(!clearFlags.isNullOrEmpty()) {
            fields.add(OperationField(AppUtil.getString(R.string.op_field_clear_flags),
                clearFlags.joinToString(separator = " ") {
                    return@joinToString mapFlagToString(it).plus("\n")
                }.trim()
            ))
        }

        if(!setFlags.isNullOrEmpty()) {
            fields.add(OperationField(AppUtil.getString(R.string.op_field_set_flags),
                setFlags.joinToString(separator = " ") {
                    return@joinToString mapFlagToString(it).plus("\n")
                }.trim()
            ))
        }

        return fields
    }

    private fun mapFlagToString(flag: Int): String {
        return when(flag) {
            AUTHORIZED_FLAG -> AppUtil.getString(R.string.op_value_authorized_flag)
            AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG -> AppUtil.getString(R.string.op_value_authorized_to_maintain_liabilities_flag)
            TRUSTLINE_CLAWBACK_ENABLED_FLAG -> AppUtil.getString(R.string.op_value_trustline_clawback_enabled_flag)
            else -> "Unknown"
        }
    }
}