package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateContractArgsV2(
    val contractIDPreimage: ContractIDPreimage,
    val executable: ContractExecutable,
    val constructorArgs: List<SCVal> = listOf()
) : Parcelable