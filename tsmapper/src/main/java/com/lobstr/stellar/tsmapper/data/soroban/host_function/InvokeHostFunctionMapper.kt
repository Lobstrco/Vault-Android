package com.lobstr.stellar.tsmapper.data.soroban.host_function

import com.lobstr.stellar.tsmapper.data.asset.AssetMapper
import com.lobstr.stellar.tsmapper.data.soroban.contract.ScMapper
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.HostFunction
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.InvokeHostFunctionOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanAuthorizationEntry
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanAuthorizedFunction
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanAuthorizedInvocation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanCredentials
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.ContractExecutable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.ContractIDPreimage
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.CreateContractArgs
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.InvokeContractArgs
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import org.stellar.sdk.Address
import org.stellar.sdk.Operation
import org.stellar.sdk.Util
import org.stellar.sdk.xdr.HostFunctionType

class InvokeHostFunctionMapper(val assetMapper: AssetMapper = AssetMapper(), val scMapper: ScMapper = ScMapper()) {

    fun mapInvokeHostFunctionOperation(operation: org.stellar.sdk.InvokeHostFunctionOperation): InvokeHostFunctionOperation =
        InvokeHostFunctionOperation(
            (operation as Operation).sourceAccount,
            mapHostFunction(operation.hostFunction),
            mutableListOf<SorobanAuthorizationEntry>().apply {
                operation.auth.forEach { add(mapSorobanAuthorizationEntry(it)) }
            }
        )

    private fun mapHostFunction(function: org.stellar.sdk.xdr.HostFunction): HostFunction =
        when (function.discriminant) {
            HostFunctionType.HOST_FUNCTION_TYPE_INVOKE_CONTRACT -> HostFunction.InvokeContract(
                mapInvokeContractArgs(function.invokeContract)
            )

            HostFunctionType.HOST_FUNCTION_TYPE_CREATE_CONTRACT -> HostFunction.CreateContract(
                mapCreateContractArgs(function.createContract)
            )

            HostFunctionType.HOST_FUNCTION_TYPE_UPLOAD_CONTRACT_WASM -> HostFunction.UploadContractWasm(
                Util.bytesToHex(function.wasm).lowercase()
            )
        }

    private fun mapInvokeContractArgs(args: org.stellar.sdk.xdr.InvokeContractArgs): InvokeContractArgs =
        InvokeContractArgs(
            scMapper.mapScAddress(Address.fromSCAddress(args.contractAddress)),
            args.functionName.scSymbol.toString(),
            mutableListOf<SCVal>().apply {
                args.args.forEach {
                    add(scMapper.mapScVal(it))
                }
            }
        )

    private fun mapCreateContractArgs(args: org.stellar.sdk.xdr.CreateContractArgs): CreateContractArgs =
        CreateContractArgs(
            mapContractIDPreimage(args.contractIDPreimage),
            mapContractExecutable(args.executable)
        )

    private fun mapContractIDPreimage(contractIDPreimage: org.stellar.sdk.xdr.ContractIDPreimage): ContractIDPreimage =
        when (contractIDPreimage.discriminant) {
            org.stellar.sdk.xdr.ContractIDPreimageType.CONTRACT_ID_PREIMAGE_FROM_ADDRESS -> ContractIDPreimage.FromAddress(
                scMapper.mapScAddress(Address.fromSCAddress(contractIDPreimage.fromAddress.address)),
                Util.bytesToHex(contractIDPreimage.fromAddress.salt.uint256).lowercase()
            )

            org.stellar.sdk.xdr.ContractIDPreimageType.CONTRACT_ID_PREIMAGE_FROM_ASSET -> ContractIDPreimage.FromAsset(
                assetMapper.mapAsset(contractIDPreimage.fromAsset)
            )
        }

    private fun mapContractExecutable(executable: org.stellar.sdk.xdr.ContractExecutable): ContractExecutable =
        when (executable.discriminant) {
            org.stellar.sdk.xdr.ContractExecutableType.CONTRACT_EXECUTABLE_WASM -> ContractExecutable.Wasm(
                Util.bytesToHex(executable.wasm_hash.hash).lowercase()
            )
            org.stellar.sdk.xdr.ContractExecutableType.CONTRACT_EXECUTABLE_STELLAR_ASSET -> ContractExecutable.StellarAsset()
        }

    private fun mapSorobanAuthorizationEntry(entry: org.stellar.sdk.xdr.SorobanAuthorizationEntry): SorobanAuthorizationEntry =
        SorobanAuthorizationEntry(mapSorobanCredentials(entry.credentials), mapSorobanAuthorizedInvocation(entry.rootInvocation))

    private fun mapSorobanCredentials(credentials: org.stellar.sdk.xdr.SorobanCredentials): SorobanCredentials =
        when (credentials.discriminant) {
            org.stellar.sdk.xdr.SorobanCredentialsType.SOROBAN_CREDENTIALS_SOURCE_ACCOUNT -> SorobanCredentials.SourceAccount()
            org.stellar.sdk.xdr.SorobanCredentialsType.SOROBAN_CREDENTIALS_ADDRESS -> SorobanCredentials.Address(
                scMapper.mapScAddress(Address.fromSCAddress(credentials.address.address)),
                credentials.address.nonce.int64.toString(),
                credentials.address.signatureExpirationLedger.uint32.number.toString(),
                scMapper.mapScVal(credentials.address.signature)
            )
        }

    private fun mapSorobanAuthorizedInvocation(invocation: org.stellar.sdk.xdr.SorobanAuthorizedInvocation) : SorobanAuthorizedInvocation =
        SorobanAuthorizedInvocation(mapSorobanAuthorizationFunction(invocation.function), mutableListOf<SorobanAuthorizedInvocation>().apply {
            invocation.subInvocations.forEach {
                add(mapSorobanAuthorizedInvocation(it))
            }
        })

    private fun mapSorobanAuthorizationFunction(function: org.stellar.sdk.xdr.SorobanAuthorizedFunction): SorobanAuthorizedFunction =
        when (function.discriminant) {
            org.stellar.sdk.xdr.SorobanAuthorizedFunctionType.SOROBAN_AUTHORIZED_FUNCTION_TYPE_CONTRACT_FN -> SorobanAuthorizedFunction.Contract(
                mapInvokeContractArgs(function.contractFn)
            )

            org.stellar.sdk.xdr.SorobanAuthorizedFunctionType.SOROBAN_AUTHORIZED_FUNCTION_TYPE_CREATE_CONTRACT_HOST_FN -> SorobanAuthorizedFunction.CreateContractHost(
                mapCreateContractArgs(function.createContractHostFn)
            )
        }
}