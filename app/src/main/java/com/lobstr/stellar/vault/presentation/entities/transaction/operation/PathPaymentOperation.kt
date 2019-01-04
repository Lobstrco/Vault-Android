package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset

data class PathPaymentOperation(
    override val sourceAccount: String?,
    val sendAsset: Asset,
    val sendMax: String,
    val destination: String,
    val destAsset: Asset,
    val destAmount: String,
    val path: List<Asset>
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readString(),
        parcel.createTypedArrayList(Asset)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeParcelable(sendAsset, flags)
        parcel.writeString(sendMax)
        parcel.writeString(destination)
        parcel.writeParcelable(destAsset, flags)
        parcel.writeString(destAmount)
        parcel.writeTypedList(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PathPaymentOperation> {
        override fun createFromParcel(parcel: Parcel): PathPaymentOperation {
            return PathPaymentOperation(parcel)
        }

        override fun newArray(size: Int): Array<PathPaymentOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["sendAsset"] = sendAsset.assetCode
        map["sendMax"] = sendMax
        map["destination"] = destination
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