package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateContractArgs(
    val contractIDPreimage: ContractIDPreimage,
    val executable: ContractExecutable
) : Parcelable
