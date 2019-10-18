package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccountMergeOperation(
    override val sourceAccount: String?,
    val destination: String
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        if (destination.isNotEmpty()) map["destination"] = destination

        return map
    }
}