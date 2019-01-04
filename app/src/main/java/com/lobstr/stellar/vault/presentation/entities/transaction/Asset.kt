package com.lobstr.stellar.vault.presentation.entities.transaction

import android.os.Parcel
import android.os.Parcelable


data class Asset(
    val assetCode: String,
    val assetType: String,
    val assetIssuer: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assetCode)
        parcel.writeString(assetType)
        parcel.writeString(assetIssuer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Asset> {
        override fun createFromParcel(parcel: Parcel): Asset {
            return Asset(parcel)
        }

        override fun newArray(size: Int): Array<Asset?> {
            return arrayOfNulls(size)
        }
    }

}