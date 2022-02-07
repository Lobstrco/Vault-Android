package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class SetOptionsOperation(
    override val sourceAccount: String?,
    val inflationDestination: String?,
    val clearFlags: Int?,
    val setFlags: Int?,
    val masterKeyWeight: Int?,
    val lowThreshold: Int?,
    val mediumThreshold: Int?,
    val highThreshold: Int?,
    val homeDomain: String?,
    val signerWeight: Int?,
    val signerPublicKey: String?
) : Operation(sourceAccount), Parcelable {

    override fun getFields(context: Context, amountFormatter: (value: String) -> String): MutableList<OperationField> {
        val fields: MutableList<OperationField> = mutableListOf()
        if (!inflationDestination.isNullOrEmpty()) fields.add(OperationField(context.getString(R.string.op_field_inflation_destination), inflationDestination, inflationDestination))
        if (!homeDomain.isNullOrEmpty()) fields.add(OperationField(context.getString(R.string.op_field_home_domain), homeDomain))
        if (clearFlags != null) fields.add(OperationField(context.getString(R.string.op_field_clear_flags), clearFlags.toString()))
        if (setFlags != null) fields.add(OperationField(context.getString(R.string.op_field_set_flags), setFlags.toString()))
        if (masterKeyWeight != null) fields.add(OperationField(context.getString(R.string.op_field_master_key_weight), masterKeyWeight.toString()))
        if (lowThreshold != null) fields.add(OperationField(context.getString(R.string.op_field_low_threshold), lowThreshold.toString()))
        if (mediumThreshold != null) fields.add(OperationField(context.getString(R.string.op_field_medium_threshold), mediumThreshold.toString()))
        if (highThreshold != null) fields.add(OperationField(context.getString(R.string.op_field_high_threshold), highThreshold.toString()))
        if (signerWeight != null) fields.add(OperationField(context.getString(R.string.op_field_signer_weight), signerWeight.toString()))
        if (signerPublicKey != null) fields.add(OperationField(context.getString(R.string.op_field_signer_public_key), signerPublicKey, signerPublicKey))

        return fields
    }
}