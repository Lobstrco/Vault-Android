package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllowTrustOperation(
    override val sourceAccount: String?,
    val trustor: String,
    val assetCode: String,
    val authorize: Boolean
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        if (trustor.isNotEmpty()) map["trustor"] = trustor
        if (assetCode.isNotEmpty()) map["assetCode"] = assetCode
        map["authorize"] = authorize.toString()

        return map
    }
}