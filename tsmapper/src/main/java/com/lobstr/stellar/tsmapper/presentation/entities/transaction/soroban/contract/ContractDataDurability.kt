package com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ContractDataDurability : Parcelable {
    class TEMPORARY : ContractDataDurability()
    class PERSISTENT : ContractDataDurability()
}