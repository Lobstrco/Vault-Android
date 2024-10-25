package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCAddress
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ContractIDPreimage : Parcelable {
    data class FromAddress(val address: SCAddress, val salt: String) : ContractIDPreimage()

    data class FromAsset(val asset: Asset) : ContractIDPreimage()
}