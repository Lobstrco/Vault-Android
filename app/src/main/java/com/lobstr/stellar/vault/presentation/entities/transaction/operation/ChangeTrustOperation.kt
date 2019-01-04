package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset

data class ChangeTrustOperation(
    override val sourceAccount: String?,
    val asset: Asset,
    val limit: String
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Asset::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeParcelable(asset, flags)
        parcel.writeString(limit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChangeTrustOperation> {
        override fun createFromParcel(parcel: Parcel): ChangeTrustOperation {
            return ChangeTrustOperation(parcel)
        }

        override fun newArray(size: Int): Array<ChangeTrustOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["asset"] = asset.assetCode
        map["limit"] = limit

        return map
    }
}