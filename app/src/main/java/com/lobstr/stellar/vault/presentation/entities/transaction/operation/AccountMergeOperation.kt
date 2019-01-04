package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable

data class AccountMergeOperation(
    override val sourceAccount: String?,
    val destination: String
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeString(destination)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountMergeOperation> {
        override fun createFromParcel(parcel: Parcel): AccountMergeOperation {
            return AccountMergeOperation(parcel)
        }

        override fun newArray(size: Int): Array<AccountMergeOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["destination"] = destination

        return map
    }
}