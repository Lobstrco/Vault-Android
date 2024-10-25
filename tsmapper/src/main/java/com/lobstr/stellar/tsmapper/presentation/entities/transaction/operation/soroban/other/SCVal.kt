package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.ContractExecutable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.error.SCError
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SCVal : Parcelable {

    data class SCVBool(val b: Boolean) : SCVal()

    class SCVVoid : SCVal()

    data class SCVError(val error: SCError) : SCVal()

    data class SCVTimePoint(val timepoint: String) : SCVal()

    data class SCVDuration(val duration: String) : SCVal()

    data class SCVAddress(val address: SCAddress) : SCVal()

    data class SCVSymbol(val sym: String) : SCVal()

    data class SCVBytes(val bytes: String) : SCVal()

    data class SCVString(val str: String) : SCVal()

    data class SCVI128(val i128: String) : SCVal()

    data class SCVU32(val u32: String) : SCVal()

    data class SCVI32(val i32: String) : SCVal()

    data class SCVU64(val u64: String) : SCVal()

    data class SCVI64(val i64: String) : SCVal()

    data class SCVU128(val u128: String) : SCVal()

    data class SCVU256(val u256: String) : SCVal()

    data class SCVI256(val i256: String) : SCVal()

    // Vec and Map are recursive so need to live
    // behind an option, due to xdrpp limitations.
    data class SCVVec(val vec: SCVec) : SCVal()

    data class SCVMap(val map: Map<SCVal, SCVal>) : SCVal()

    data class SCVContractInstance(val executable: ContractExecutable, val storage: Map<SCVal, SCVal>) :
        SCVal()

    // Special SCVals reserved for system-constructed contract-data
    // ledger keys, not generally usable elsewhere.
    class SCVLedgerKeyContractInstance : SCVal()

    data class SCVLedgerKeyNonce(val nonceKey: String) : SCVal()
}