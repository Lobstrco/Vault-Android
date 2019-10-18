package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ManageDataOperation(
    override val sourceAccount: String?,
    val name: String,
    val value: ByteArray
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["name"] = name
        map["value"] = String(value)

        return map
    }
}