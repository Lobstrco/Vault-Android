package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable

data class ManageDataOperation(
    override val sourceAccount: String?,
    val name: String,
    val value: ByteArray
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createByteArray()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeString(name)
        parcel.writeByteArray(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ManageDataOperation> {
        override fun createFromParcel(parcel: Parcel): ManageDataOperation {
            return ManageDataOperation(parcel)
        }

        override fun newArray(size: Int): Array<ManageDataOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["name"] = name
        map["value"] = String(value)

        return map
    }
}