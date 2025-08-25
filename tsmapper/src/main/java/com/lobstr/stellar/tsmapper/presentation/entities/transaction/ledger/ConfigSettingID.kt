package com.lobstr.stellar.tsmapper.presentation.entities.transaction.ledger

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ConfigSettingID : Parcelable {
    class CONFIG_SETTING_CONTRACT_MAX_SIZE_BYTES : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_COMPUTE_V0 : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_LEDGER_COST_V0 : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_HISTORICAL_DATA_V0 : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_EVENTS_V0 : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_BANDWIDTH_V0 : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_COST_PARAMS_CPU_INSTRUCTIONS : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_COST_PARAMS_MEMORY_BYTES : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_DATA_KEY_SIZE_BYTES : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_DATA_ENTRY_SIZE_BYTES : ConfigSettingID()
    class CONFIG_SETTING_STATE_ARCHIVAL : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_EXECUTION_LANES : ConfigSettingID()
    class CONFIG_SETTING_LIVE_SOROBAN_STATE_SIZE_WINDOW : ConfigSettingID()
    class CONFIG_SETTING_EVICTION_ITERATOR : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_PARALLEL_COMPUTE_V0 : ConfigSettingID()
    class CONFIG_SETTING_CONTRACT_LEDGER_COST_EXT_V0 : ConfigSettingID()
    class CONFIG_SETTING_SCP_TIMING : ConfigSettingID()
}