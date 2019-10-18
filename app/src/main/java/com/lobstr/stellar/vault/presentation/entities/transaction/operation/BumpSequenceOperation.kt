package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BumpSequenceOperation(
    override val sourceAccount: String?,
    val bumpTo: Long
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["bumpTo"] = bumpTo.toString()

        return map
    }
}