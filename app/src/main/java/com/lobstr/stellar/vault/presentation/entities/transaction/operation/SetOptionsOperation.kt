package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcel
import android.os.Parcelable

data class SetOptionsOperation(
    override val sourceAccount: String?,
    val inflationDestination: String,
    val clearFlags: Int?,
    val setFlags: Int?,
    val masterKeyWeight: Int?,
    val lowThreshold: Int?,
    val mediumThreshold: Int?,
    val highThreshold: Int?,
    val homeDomain: String?,
    val signerWeight: Int?
) : Operation(sourceAccount), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(sourceAccount)
        parcel.writeString(inflationDestination)
        parcel.writeValue(clearFlags)
        parcel.writeValue(setFlags)
        parcel.writeValue(masterKeyWeight)
        parcel.writeValue(lowThreshold)
        parcel.writeValue(mediumThreshold)
        parcel.writeValue(highThreshold)
        parcel.writeString(homeDomain)
        parcel.writeValue(signerWeight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SetOptionsOperation> {
        override fun createFromParcel(parcel: Parcel): SetOptionsOperation {
            return SetOptionsOperation(parcel)
        }

        override fun newArray(size: Int): Array<SetOptionsOperation?> {
            return arrayOfNulls(size)
        }
    }

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        map["sourceAccount"] = sourceAccount
        map["inflationDestination"] = inflationDestination
        map["clearFlags"] = clearFlags.toString()
        map["setFlags"] = setFlags.toString()
        map["masterKeyWeight"] = masterKeyWeight.toString()
        map["lowThreshold"] = lowThreshold.toString()
        map["mediumThreshold"] = mediumThreshold.toString()
        map["highThreshold"] = highThreshold.toString()
        map["homeDomain"] = homeDomain.toString()
        map["signerWeight"] = signerWeight.toString()

        return map
    }
}