package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCAddress
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvokeContractArgs(
    val contractAddress: SCAddress,
    val functionName: String,
    val args: List<SCVal> = listOf()
) : Parcelable
