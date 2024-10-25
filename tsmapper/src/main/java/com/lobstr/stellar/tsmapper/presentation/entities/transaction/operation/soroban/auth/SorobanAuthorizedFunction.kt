package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.CreateContractArgs
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.CreateContractArgsV2
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.InvokeContractArgs
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SorobanAuthorizedFunction : Parcelable {
    data class Contract(val args: InvokeContractArgs) : SorobanAuthorizedFunction()

    data class CreateContractHost(val args: CreateContractArgs) : SorobanAuthorizedFunction()

    data class CreateContractV2Host(val args: CreateContractArgsV2) : SorobanAuthorizedFunction()
}