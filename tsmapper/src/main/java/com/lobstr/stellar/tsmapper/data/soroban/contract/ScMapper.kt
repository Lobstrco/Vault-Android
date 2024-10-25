package com.lobstr.stellar.tsmapper.data.soroban.contract

import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.ContractExecutable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.error.SCError
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.error.SCErrorCode
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCAddress
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVec
import org.stellar.sdk.Address
import org.stellar.sdk.StrKey
import org.stellar.sdk.Util
import org.stellar.sdk.scval.Scv
import org.stellar.sdk.xdr.SCContractInstance

class ScMapper {

    fun mapScVal(scval: org.stellar.sdk.xdr.SCVal): SCVal = when (scval.discriminant) {
        org.stellar.sdk.xdr.SCValType.SCV_BOOL -> SCVal.SCVBool(Scv.fromBoolean(scval))
        org.stellar.sdk.xdr.SCValType.SCV_VOID -> SCVal.SCVVoid()
        org.stellar.sdk.xdr.SCValType.SCV_ERROR -> SCVal.SCVError(mapScError(Scv.fromError(scval)))
        org.stellar.sdk.xdr.SCValType.SCV_TIMEPOINT -> SCVal.SCVTimePoint(Scv.fromTimePoint(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_DURATION -> SCVal.SCVDuration(Scv.fromDuration(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_ADDRESS -> SCVal.SCVAddress(mapScAddress(Scv.fromAddress(scval)))
        org.stellar.sdk.xdr.SCValType.SCV_SYMBOL -> SCVal.SCVSymbol(Scv.fromSymbol(scval))
        org.stellar.sdk.xdr.SCValType.SCV_BYTES -> SCVal.SCVBytes(Util.bytesToHex(Scv.fromBytes(scval)).lowercase())
        org.stellar.sdk.xdr.SCValType.SCV_STRING -> SCVal.SCVString(scval.str.scString.toString())
        org.stellar.sdk.xdr.SCValType.SCV_I128 -> SCVal.SCVI128(Scv.fromInt128(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_U32 -> SCVal.SCVU32(Scv.fromUint32(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_I32 -> SCVal.SCVI32(Scv.fromInt32(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_U64 -> SCVal.SCVU64(Scv.fromUint64(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_I64 -> SCVal.SCVI64(Scv.fromInt64(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_U128 -> SCVal.SCVU128(Scv.fromUint128(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_U256 -> SCVal.SCVU256(Scv.fromUint256(scval).toString())
        org.stellar.sdk.xdr.SCValType.SCV_I256 -> SCVal.SCVI256(Scv.fromInt256(scval).toString())

        // Vec and Map are recursive so need to live
        // behind an option, due to xdrpp limitations.
        org.stellar.sdk.xdr.SCValType.SCV_VEC -> SCVal.SCVVec(mapScVec(Scv.fromVec(scval)))
        org.stellar.sdk.xdr.SCValType.SCV_MAP -> SCVal.SCVMap(mutableMapOf<SCVal, SCVal>().apply {
            Scv.fromMap(scval).forEach {
                put(mapScVal(it.key), mapScVal(it.value))
            }
        })

        org.stellar.sdk.xdr.SCValType.SCV_CONTRACT_INSTANCE -> mapScContractInstance(Scv.fromContractInstance(scval))
        org.stellar.sdk.xdr.SCValType.SCV_LEDGER_KEY_CONTRACT_INSTANCE -> SCVal.SCVLedgerKeyContractInstance()
        org.stellar.sdk.xdr.SCValType.SCV_LEDGER_KEY_NONCE -> SCVal.SCVLedgerKeyNonce(Scv.fromLedgerKeyNonce(scval).toString())
    }

    private fun mapScVec(data: Collection<org.stellar.sdk.xdr.SCVal>): SCVec =
        SCVec(mutableListOf<SCVal>().apply {
            data.forEach {
                add(mapScVal(it))
            }
        })

    fun mapScError(error: org.stellar.sdk.xdr.SCError): SCError = when (error.discriminant) {
        org.stellar.sdk.xdr.SCErrorType.SCE_CONTRACT -> SCError.SCEContract(error.contractCode.uint32.number.toString())
        org.stellar.sdk.xdr.SCErrorType.SCE_WASM_VM -> SCError.SCEWasmVm(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_CONTEXT -> SCError.SCEContext(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_STORAGE -> SCError.SCEStorage(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_OBJECT -> SCError.SCEObject(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_CRYPTO -> SCError.SCECrypto(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_EVENTS -> SCError.SCEEvents(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_BUDGET -> SCError.SCEBudget(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_VALUE -> SCError.SCEValue(mapScErrorCode(error.code))
        org.stellar.sdk.xdr.SCErrorType.SCE_AUTH -> SCError.SCEAuth(mapScErrorCode(error.code))
    }

    fun mapScErrorCode(code: org.stellar.sdk.xdr.SCErrorCode): SCErrorCode = when (code) {
        org.stellar.sdk.xdr.SCErrorCode.SCEC_ARITH_DOMAIN -> SCErrorCode.SCECArithDomain()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_INDEX_BOUNDS -> SCErrorCode.SCECIndexBounds()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_INVALID_INPUT -> SCErrorCode.SCECInvalidInput()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_MISSING_VALUE -> SCErrorCode.SCECMissingValue()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_EXISTING_VALUE -> SCErrorCode.SCECExistingValue()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_EXCEEDED_LIMIT -> SCErrorCode.SCECExceededLimit()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_INVALID_ACTION -> SCErrorCode.SCECInvalidAction()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_INTERNAL_ERROR -> SCErrorCode.SCECInternalError()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_UNEXPECTED_TYPE -> SCErrorCode.SCECUnexpectedType()
        org.stellar.sdk.xdr.SCErrorCode.SCEC_UNEXPECTED_SIZE -> SCErrorCode.SCECUnexpectedSize()
    }

    fun mapScAddress(address: Address): SCAddress =
        when (address.addressType) {
            Address.AddressType.ACCOUNT ->
                SCAddress.Account(StrKey.encodeEd25519PublicKey(address.bytes))
            Address.AddressType.CONTRACT ->
                SCAddress.Contract(StrKey.encodeContract(address.bytes))
        }

    fun mapScAddress(address: org.stellar.sdk.xdr.SCAddress): SCAddress =
        when (address.discriminant) {
            org.stellar.sdk.xdr.SCAddressType.SC_ADDRESS_TYPE_ACCOUNT ->
                SCAddress.Account(StrKey.encodeEd25519PublicKey(address.accountId.accountID.ed25519.uint256))
            org.stellar.sdk.xdr.SCAddressType.SC_ADDRESS_TYPE_CONTRACT ->
                SCAddress.Contract(StrKey.encodeContract(address.contractId.toXdrByteArray()))
        }

    private fun mapScContractInstance(instance: SCContractInstance) : SCVal.SCVContractInstance =
        SCVal.SCVContractInstance(
            mapContractExecutable(instance.executable),
            mutableMapOf<SCVal, SCVal>().apply {
                instance.storage.scMap.forEach {
                    put(mapScVal(it.key), mapScVal(it.`val`))
                }
            }
        )

    private fun mapContractExecutable(executable: org.stellar.sdk.xdr.ContractExecutable): ContractExecutable =
        when (executable.discriminant) {
            org.stellar.sdk.xdr.ContractExecutableType.CONTRACT_EXECUTABLE_WASM -> ContractExecutable.Wasm(
                Util.bytesToHex(executable.wasm_hash.hash).lowercase()
            )
            org.stellar.sdk.xdr.ContractExecutableType.CONTRACT_EXECUTABLE_STELLAR_ASSET -> ContractExecutable.StellarAsset()
        }
}