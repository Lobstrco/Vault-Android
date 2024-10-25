package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.CreateContractArgs
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.CreateContractArgsV2
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.InvokeContractArgs
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class HostFunction : Parcelable {

    data class InvokeContract(val args: InvokeContractArgs) : HostFunction()

    data class CreateContract(val args: CreateContractArgs) : HostFunction()

    data class CreateContractV2(val args: CreateContractArgsV2) : HostFunction()

    data class UploadContractWasm(val wasm: String) : HostFunction()
}