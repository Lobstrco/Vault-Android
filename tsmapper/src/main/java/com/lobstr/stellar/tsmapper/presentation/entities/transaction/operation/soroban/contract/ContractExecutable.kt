package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ContractExecutable : Parcelable {
    data class Wasm(val wasmHash: String) : ContractExecutable()

    class StellarAsset : ContractExecutable()
}