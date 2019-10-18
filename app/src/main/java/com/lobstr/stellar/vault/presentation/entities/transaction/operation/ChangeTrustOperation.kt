package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChangeTrustOperation(
    override val sourceAccount: String?,
    val asset: Asset,
    val limit: String
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["asset"] = asset.assetCode
        map["limit"] = limit

        return map
    }
}