package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCAddress
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SorobanCredentials : Parcelable {
    class SourceAccount : SorobanCredentials()

    data class Address(
        val address: SCAddress,
        val nonce: String,
        val signatureExpirationLedger: String,
        val signature: SCVal
    ) : SorobanCredentials()
}