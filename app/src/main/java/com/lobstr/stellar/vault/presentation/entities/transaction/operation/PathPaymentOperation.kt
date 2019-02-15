package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PathPaymentOperation(
    override val sourceAccount: String?,
    val sendAsset: Asset,
    val sendMax: String,
    val destination: String,
    val destAsset: Asset,
    val destAmount: String,
    val path: List<Asset>
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sendAsset"] = sendAsset.assetCode
        map["sendMax"] = sendMax
        if (destination.isNotEmpty()) map["destination"] = destination
        map["destAsset"] = destAsset.assetCode
        map["destAmount"] = destAmount

        var pathStr = ""
        path.forEach {
            pathStr += it.assetCode + " "
        }
        map["path"] = pathStr

        return map
    }
}