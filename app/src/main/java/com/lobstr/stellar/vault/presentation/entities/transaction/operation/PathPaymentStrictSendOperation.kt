package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PathPaymentStrictSendOperation(
    override val sourceAccount: String?,
    val sendAsset: Asset,
    val sendAmount: String,
    val destination: String,
    val destAsset: Asset,
    val destMin: String,
    val path: List<Asset>
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sendAsset"] = sendAsset.assetCode
        map["sendAmount"] = sendAmount
        if (destination.isNotEmpty()) map["destination"] = destination
        map["destAsset"] = destAsset.assetCode
        map["destMin"] = destMin

        var pathStr = ""
        path.forEach {
            pathStr += it.assetCode + " "
        }
        map["path"] = pathStr

        return map
    }
}