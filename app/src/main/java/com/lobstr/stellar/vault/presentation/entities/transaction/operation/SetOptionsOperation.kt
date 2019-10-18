package com.lobstr.stellar.vault.presentation.entities.transaction.operation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SetOptionsOperation(
    override val sourceAccount: String?,
    val inflationDestination: String?,
    val clearFlags: Int?,
    val setFlags: Int?,
    val masterKeyWeight: Int?,
    val lowThreshold: Int?,
    val mediumThreshold: Int?,
    val highThreshold: Int?,
    val homeDomain: String?,
    val signerWeight: Int?
) : Operation(sourceAccount), Parcelable {

    fun getFieldsMap(): Map<String, String?> {
        val map: MutableMap<String, String?> = mutableMapOf()
        if (!inflationDestination.isNullOrEmpty()) map["inflationDestination"] = inflationDestination
        if (!homeDomain.isNullOrEmpty()) map["homeDomain"] = homeDomain
        if (clearFlags != null) map["clearFlags"] = clearFlags.toString()
        if (setFlags != null) map["setFlags"] = setFlags.toString()
        if (masterKeyWeight != null) map["masterKeyWeight"] = masterKeyWeight.toString()
        if (lowThreshold != null) map["lowThreshold"] = lowThreshold.toString()
        if (mediumThreshold != null) map["mediumThreshold"] = mediumThreshold.toString()
        if (highThreshold != null) map["highThreshold"] = highThreshold.toString()
        if (signerWeight != null) map["signerWeight"] = signerWeight.toString()

        return map
    }
}