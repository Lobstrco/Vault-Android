package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable

data class CreateAccountOperation(
    override val sourceAccount: String?,
    val destination: String,
    val startingBalance: String
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeString(destination)
        parcel.writeString(startingBalance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreateAccountOperation> {
        override fun createFromParcel(parcel: Parcel): CreateAccountOperation {
            return CreateAccountOperation(parcel)
        }

        override fun newArray(size: Int): Array<CreateAccountOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["destination"] = destination
        map["startingBalance"] = startingBalance

        return map
    }
}