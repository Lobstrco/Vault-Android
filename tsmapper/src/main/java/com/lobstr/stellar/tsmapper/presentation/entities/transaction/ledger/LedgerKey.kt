package com.lobstr.stellar.tsmapper.presentation.entities.transaction.ledger

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCAddress
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.contract.ContractDataDurability
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class LedgerKey : Parcelable {

    data class Account(val accountId: String) : LedgerKey()

    data class Trustline(val accountId: String, val asset: Asset) : LedgerKey()

    data class Offer(val sellerID: String, val offerID: String) : LedgerKey()

    data class Data(val accountID: String, val dataName: String) : LedgerKey()

    data class ClaimableBalance(val balanceID: String) : LedgerKey()

    data class LiquidityPool(val liquidityPoolID: String) : LedgerKey()

    data class ContractData(val contract: SCAddress, val key: SCVal, val durability: ContractDataDurability) : LedgerKey()

    data class ContractCode(val hash: String) : LedgerKey()

    data class ConfigSettings(val configSettingID: ConfigSettingID) : LedgerKey()

    data class Ttl(val keyHash: String) : LedgerKey()
}