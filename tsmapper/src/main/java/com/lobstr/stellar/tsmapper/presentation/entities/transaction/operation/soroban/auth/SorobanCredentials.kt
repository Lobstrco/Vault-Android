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

    // CAP-71 (Protocol 27): address-bound signature variant. Same payload shape as [Address];
    // kept as a distinct type so the auth scheme (discriminant) is never conflated with V1.
    data class AddressV2(
        val address: SCAddress,
        val nonce: String,
        val signatureExpirationLedger: String,
        val signature: SCVal
    ) : SorobanCredentials()

    // CAP-71 (Protocol 27): delegated / multi-party signing. Base address credentials plus a
    // (recursive) tree of delegate signatures.
    data class AddressWithDelegates(
        val address: SCAddress,
        val nonce: String,
        val signatureExpirationLedger: String,
        val signature: SCVal,
        val delegates: List<SorobanDelegateSignature>
    ) : SorobanCredentials()
}