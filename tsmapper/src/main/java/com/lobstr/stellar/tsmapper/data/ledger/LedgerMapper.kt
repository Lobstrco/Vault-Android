package com.lobstr.stellar.tsmapper.data.ledger

import com.lobstr.stellar.tsmapper.data.asset.AssetMapper
import com.lobstr.stellar.tsmapper.data.soroban.contract.ScMapper
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.ledger.ConfigSettingID
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.contract.ContractDataDurability
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.ledger.LedgerFootprint
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.ledger.LedgerKey
import org.stellar.sdk.Address
import org.stellar.sdk.StrKey
import org.stellar.sdk.Util
import org.stellar.sdk.xdr.ClaimableBalanceIDType
import org.stellar.sdk.xdr.LedgerEntryType

class LedgerMapper(val assetMapper: AssetMapper = AssetMapper(), val scMapper: ScMapper = ScMapper()) {

    fun mapLedgerFootprint(footprint: org.stellar.sdk.xdr.LedgerFootprint): LedgerFootprint {
        return LedgerFootprint(
            readOnly = mapLedgerKeys(footprint.readOnly),
            readWrite = mapLedgerKeys(footprint.readWrite)
        )
    }

    private fun mapLedgerKeys(keys: Array<org.stellar.sdk.xdr.LedgerKey>): List<LedgerKey> {
        return mutableListOf<LedgerKey>().apply {
            keys.forEach {
                add(mapLedgerKey(it))
            }
        }
    }

    fun mapLedgerKey(key: org.stellar.sdk.xdr.LedgerKey): LedgerKey {
        return when (key.discriminant) {
            LedgerEntryType.ACCOUNT -> {
                LedgerKey.Account(StrKey.encodeEd25519PublicKey(key.account.accountID.accountID.ed25519.uint256))
            }

            LedgerEntryType.TRUSTLINE -> {
                LedgerKey.Trustline(
                    StrKey.encodeEd25519PublicKey(key.trustLine.accountID.accountID.ed25519.uint256),
                    assetMapper.mapTrustLineAsset(key.trustLine.asset)
                )
            }

            LedgerEntryType.OFFER -> {
                LedgerKey.Offer(
                    StrKey.encodeEd25519PublicKey(key.offer.sellerID.accountID.ed25519.uint256),
                    key.offer.offerID.int64.toString()
                )
            }

            LedgerEntryType.DATA -> {
                LedgerKey.Data(
                    StrKey.encodeEd25519PublicKey(key.data.accountID.accountID.ed25519.uint256),
                    key.data.dataName.string64.toString()
                )
            }

            LedgerEntryType.CLAIMABLE_BALANCE -> {
                LedgerKey.ClaimableBalance(
                    when (key.claimableBalance.balanceID.discriminant) {
                        ClaimableBalanceIDType.CLAIMABLE_BALANCE_ID_TYPE_V0 -> Util.bytesToHex(
                            key.claimableBalance.balanceID.v0.hash
                        ).lowercase()
                        else -> ""
                    }
                )
            }

            LedgerEntryType.LIQUIDITY_POOL -> {
                LedgerKey.LiquidityPool(
                    Util.bytesToHex(key.liquidityPool.liquidityPoolID.poolID.hash).lowercase()
                )
            }

            LedgerEntryType.CONTRACT_DATA -> {
                LedgerKey.ContractData(
                    scMapper.mapScAddress(Address.fromSCAddress(key.contractData.contract)),
                    scMapper.mapScVal(key.contractData.key),
                    mapContractDataDurability(key.contractData.durability)
                )
            }

            LedgerEntryType.CONTRACT_CODE -> {
                LedgerKey.ContractCode(Util.bytesToHex(key.contractCode.hash.hash).lowercase())
            }

            LedgerEntryType.CONFIG_SETTING -> {
                LedgerKey.ConfigSettings(mapConfigSettingsID(key.configSetting.configSettingID))
            }

            LedgerEntryType.TTL -> {
                LedgerKey.Ttl(Util.bytesToHex(key.ttl.keyHash.hash).lowercase())
            }
        }
    }

    private fun mapConfigSettingsID(configSettingID: org.stellar.sdk.xdr.ConfigSettingID): ConfigSettingID =
        when (configSettingID) {
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_MAX_SIZE_BYTES -> ConfigSettingID.CONFIG_SETTING_CONTRACT_MAX_SIZE_BYTES()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_COMPUTE_V0 -> ConfigSettingID.CONFIG_SETTING_CONTRACT_COMPUTE_V0()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_LEDGER_COST_V0 -> ConfigSettingID.CONFIG_SETTING_CONTRACT_LEDGER_COST_V0()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_HISTORICAL_DATA_V0 -> ConfigSettingID.CONFIG_SETTING_CONTRACT_HISTORICAL_DATA_V0()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_EVENTS_V0 -> ConfigSettingID.CONFIG_SETTING_CONTRACT_EVENTS_V0()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_BANDWIDTH_V0 -> ConfigSettingID.CONFIG_SETTING_CONTRACT_BANDWIDTH_V0()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_COST_PARAMS_CPU_INSTRUCTIONS -> ConfigSettingID.CONFIG_SETTING_CONTRACT_COST_PARAMS_CPU_INSTRUCTIONS()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_COST_PARAMS_MEMORY_BYTES -> ConfigSettingID.CONFIG_SETTING_CONTRACT_COST_PARAMS_MEMORY_BYTES()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_DATA_KEY_SIZE_BYTES -> ConfigSettingID.CONFIG_SETTING_CONTRACT_DATA_KEY_SIZE_BYTES()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_DATA_ENTRY_SIZE_BYTES -> ConfigSettingID.CONFIG_SETTING_CONTRACT_DATA_ENTRY_SIZE_BYTES()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_STATE_ARCHIVAL -> ConfigSettingID.CONFIG_SETTING_STATE_ARCHIVAL()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_EXECUTION_LANES -> ConfigSettingID.CONFIG_SETTING_CONTRACT_EXECUTION_LANES()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_LIVE_SOROBAN_STATE_SIZE_WINDOW -> ConfigSettingID.CONFIG_SETTING_LIVE_SOROBAN_STATE_SIZE_WINDOW()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_EVICTION_ITERATOR -> ConfigSettingID.CONFIG_SETTING_EVICTION_ITERATOR()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_PARALLEL_COMPUTE_V0 -> ConfigSettingID.CONFIG_SETTING_CONTRACT_PARALLEL_COMPUTE_V0()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_CONTRACT_LEDGER_COST_EXT_V0 -> ConfigSettingID.CONFIG_SETTING_CONTRACT_LEDGER_COST_EXT_V0()
            org.stellar.sdk.xdr.ConfigSettingID.CONFIG_SETTING_SCP_TIMING -> ConfigSettingID.CONFIG_SETTING_SCP_TIMING()
        }

    private fun mapContractDataDurability(durability: org.stellar.sdk.xdr.ContractDataDurability): ContractDataDurability =
        when (durability) {
            org.stellar.sdk.xdr.ContractDataDurability.TEMPORARY -> ContractDataDurability.TEMPORARY()
            org.stellar.sdk.xdr.ContractDataDurability.PERSISTENT -> ContractDataDurability.PERSISTENT()
        }
}