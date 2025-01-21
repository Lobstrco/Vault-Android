package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanAuthorizationEntry
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanAuthorizedFunction
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanAuthorizedInvocation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth.SorobanCredentials
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.ContractExecutable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.ContractIDPreimage
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.CreateContractArgs
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.CreateContractArgsV2
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.contract.InvokeContractArgs
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.error.SCError
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCAddress
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVec
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvokeHostFunctionOperation(
    override val sourceAccount: String?,
    val hostFunction: HostFunction,
    val auth: List<SorobanAuthorizationEntry>,
    // Format Helpers.
    private var argPostfixFormat: String = "",
    private var arrayArgPostfixFormat: String = ""
) : Operation(sourceAccount), Parcelable {
    override fun getFields(
        context: Context,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        // Check Postfix format and use default if needed.
        setKeyPostfixFormat(
            argPostfixFormat.ifEmpty { context.getString(R.string.key_arg_postfix) },
            arrayArgPostfixFormat.ifEmpty { context.getString(R.string.key_array_arg_postfix) }
        )

        val fields: MutableList<OperationField> = mutableListOf()

        // Host Function.
        when (hostFunction) {
            is HostFunction.InvokeContract -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_function),
                        context.getString(R.string.op_value_invoke_contract)
                    )
                )
                getInvokeContractArgsFields(context, fields, hostFunction.args, amountFormatter)
            }

            is HostFunction.CreateContract -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_function),
                        context.getString(R.string.op_value_create_contract)
                    )
                )
                getCreateContractArgsFields(context, fields, hostFunction.args, amountFormatter)
            }

            is HostFunction.CreateContractV2 -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_function),
                        context.getString(R.string.op_value_create_contract)
                    )
                )
                getCreateContractArgsV2Fields(context, fields, hostFunction.args, amountFormatter)
            }

            is HostFunction.UploadContractWasm -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_function),
                        context.getString(R.string.op_value_upload_contract_wasm)
                    )
                )
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_wasm),
                        hostFunction.wasm
                    )
                )
            }
        }

        // Auth.
        getAuthFields(context, fields, auth, amountFormatter)

        return fields
    }

    private fun getCreateContractArgsFields(
        context: Context,
        fields: MutableList<OperationField>,
        args: CreateContractArgs,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        getContractIdPreimageFields(context, fields, args.contractIDPreimage, amountFormatter)
        getContractExecutableFields(context, fields, args.executable)

        return fields
    }

    private fun getCreateContractArgsV2Fields(
        context: Context,
        fields: MutableList<OperationField>,
        args: CreateContractArgsV2,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        getContractIdPreimageFields(context, fields, args.contractIDPreimage, amountFormatter)
        getContractExecutableFields(context, fields, args.executable)

//        args.constructorArgs.forEachIndexed { index, scVal ->
//            getScValFields(
//                context,
//                fields,
//                scVal,
//                amountFormatter,
//                index + 1,
//                keyPostfix = " ${String.format(argPostfixFormat, index + 1)}"
//            )
//        }

        return fields
    }

    private fun getInvokeContractArgsFields(
        context: Context,
        fields: MutableList<OperationField>,
        args: InvokeContractArgs,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        getScAddressFields(context, fields, args.contractAddress)
        fields.add(
            OperationField(
                context.getString(R.string.op_field_function_name),
                args.functionName
            )
        )

//        args.args.forEachIndexed { index, scVal ->
//            getScValFields(
//                context,
//                fields,
//                scVal,
//                amountFormatter,
//                index + 1,
//                keyPostfix = " ${String.format(argPostfixFormat, index + 1)}"
//            )
//        }

        return fields
    }

    private fun getContractIdPreimageFields(
        context: Context,
        fields: MutableList<OperationField>, contractIDPreimage: ContractIDPreimage,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        when (contractIDPreimage) {
            is ContractIDPreimage.FromAddress -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_contract_id_preimage),
                        context.getString(R.string.op_value_from_address)
                    )
                )
                getScAddressFields(context, fields, contractIDPreimage.address)
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_salt),
                        contractIDPreimage.salt
                    )
                )
            }

            is ContractIDPreimage.FromAsset -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_contract_id_preimage),
                        context.getString(R.string.op_value_from_asset)
                    )
                )
                mapAssetFields(context, fields, contractIDPreimage.asset, amountFormatter)
            }
        }

        return fields
    }

    /**
     * @param keyPostfix empty or (arg 1) or (array, arg 1).
     */
    private fun getScValFields(
        context: Context,
        fields: MutableList<OperationField>,
        scVal: SCVal,
        amountFormatter: (value: String) -> String,
        argIndex: Int = -1,
        keyPrefix: String = "", keyPostfix: String = ""
    ): MutableList<OperationField> {
        scVal.apply {
            when (this) {
                is SCVal.SCVBool -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_bool) + keyPostfix,
                    b.toString()
                ))
                is SCVal.SCVTimePoint -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_time_point) + keyPostfix,
                    timepoint
                ))
                is SCVal.SCVDuration -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_duration) + keyPostfix,
                    duration
                ))
                is SCVal.SCVSymbol -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_symbol) + keyPostfix,
                    sym
                ))
                is SCVal.SCVString -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_string) + keyPostfix,
                    str
                ))
                is SCVal.SCVBytes -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_bytes) + keyPostfix,
                    bytes
                ))
                is SCVal.SCVAddress -> getScAddressFields(context, fields, address, keyPrefix, keyPostfix)
                is SCVal.SCVI128 -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_i128) + keyPostfix, i128
                ))
                is SCVal.SCVU32 -> fields.add(OperationField(
                    keyPrefix+context.getString(R.string.op_field_u32)+keyPostfix, u32
                ))
                is SCVal.SCVI32 -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_i32) + keyPostfix, i32
                ))
                is SCVal.SCVU64 -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_u64) + keyPostfix, u64
                ))
                is SCVal.SCVI64 -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_i64) + keyPostfix, i64
                ))
                is SCVal.SCVU128 -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_u128) + keyPostfix, u128
                ))
                is SCVal.SCVU256 -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_u256) + keyPostfix, u256
                ))
                is SCVal.SCVI256 -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_i256) + keyPostfix, i256
                ))
                is SCVal.SCVContractInstance -> {
                    getContractExecutableFields(context, fields, executable, keyPrefix, keyPostfix)
                    // Ignore.
//                    storage.forEach { mapVal ->
//                        getScValFields(context, fields, mapVal.key, amountFormatter, keyPrefix = "Key: ", keyPostfix = keyPostfix)
//                        getScValFields(context, fields, mapVal.value, amountFormatter, keyPrefix = "Value: ", keyPostfix = keyPostfix)
//                    }
                }
                is SCVal.SCVLedgerKeyContractInstance -> {/*Ignore*/}
                is SCVal.SCVLedgerKeyNonce -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_nonce_key) + keyPostfix,
                    nonceKey
                ))

                is SCVal.SCVVoid -> {/*Ignore*/}
                is SCVal.SCVError -> fields.add(OperationField(
                    keyPrefix + context.getString(R.string.op_field_error) + keyPostfix,
                    when (error) {
                        is SCError.SCEContract -> error.contractCode
                        is SCError.SCErrorWithCode -> error.code.descr // Handle Specific error
                    }
                ))

                is SCVal.SCVVec -> {
                    getScVecFields(
                        context,
                        fields,
                        vec,
                        amountFormatter,
                        argIndex,
                        keyPrefix,
                        keyPostfix = " ${String.format(arrayArgPostfixFormat, argIndex)}"
                    )
                }
                is SCVal.SCVMap -> {
                    // Ignore.
//                     map.forEach { mapVal ->
//                        getScValFields(context, fields, mapVal.key, amountFormatter, keyPrefix = "Key: ", keyPostfix = keyPostfix)
//                        getScValFields(context, fields, mapVal.value, amountFormatter, keyPrefix = "Value: ", keyPostfix = keyPostfix)
//                    }
                }
            }
        }

        return fields
    }

    private fun getScVecFields(
        context: Context,
        fields: MutableList<OperationField>, vec: SCVec,
        amountFormatter: (value: String) -> String,
        argIndex: Int = -1,
        keyPrefix: String = "",
        keyPostfix: String = ""
    ): MutableList<OperationField> {
        vec.scVec.forEach {
            getScValFields(context, fields, it, amountFormatter, argIndex, keyPrefix, keyPostfix)
        }

        return fields
    }

    private fun getContractExecutableFields(
        context: Context,
        fields: MutableList<OperationField>, executable: ContractExecutable,
        keyPrefix: String = "",
        keyPostfix: String = ""
    ): MutableList<OperationField> {
        when (executable) {
            is ContractExecutable.Wasm -> {
                fields.add(
                    OperationField(
                        keyPrefix + context.getString(R.string.op_field_contract_executable) + keyPostfix,
                        context.getString(R.string.op_value_wasm)
                    )
                )
                fields.add(
                    OperationField(
                        keyPrefix + context.getString(R.string.op_field_wasm_hash) + keyPostfix,
                        executable.wasmHash
                    )
                )
            }

            is ContractExecutable.StellarAsset -> {
                fields.add(
                    OperationField(
                        keyPrefix + context.getString(R.string.op_field_contract_executable) + keyPostfix,
                        context.getString(R.string.op_value_stellar_asset)
                    )
                )
            }
        }

        return fields
    }

    private fun getScAddressFields(
        context: Context,
        fields: MutableList<OperationField>, address: SCAddress,
        keyPrefix: String = "",
        keyPostfix: String = ""
    ): MutableList<OperationField> {
        when (address) {
            is SCAddress.Account -> fields.add(
                OperationField(
                    keyPrefix + context.getString(R.string.op_field_account_id) + keyPostfix,
                    address.accountId,
                    address.accountId
                )
            )

            is SCAddress.Contract -> fields.add(
                OperationField(
                    keyPrefix + context.getString(R.string.op_field_contract_id) + keyPostfix,
                    address.contractId
                )
            )
        }
        return fields
    }

    private fun getAuthFields(
        context: Context,
        fields: MutableList<OperationField>, auth: List<SorobanAuthorizationEntry>,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        auth.forEach {
//            getCredentialsFields(context, fields, it.credentials, amountFormatter)
            getAuthorizedInvocationFields(context, fields, it.rootInvocation, amountFormatter)
        }
        return fields
    }

    private fun getCredentialsFields(
        context: Context,
        fields: MutableList<OperationField>, credentials: SorobanCredentials,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        when (credentials) {
            is SorobanCredentials.SourceAccount -> fields.add(
                OperationField(
                    context.getString(R.string.op_field_credentials),
                    context.getString(R.string.op_value_source_account)
                )
            )

            is SorobanCredentials.Address -> {
                fields.add(OperationField(
                    context.getString(R.string.op_field_credentials),
                    context.getString(R.string.op_value_address)
                ))

                getScAddressFields(context, fields, credentials.address)

                fields.add(OperationField(
                    context.getString(R.string.op_field_nonce),
                    credentials.nonce
                ))

                fields.add(OperationField(
                    context.getString(R.string.op_field_signature_expiration_ledger),
                    credentials.signatureExpirationLedger
                ))

                getScValFields(context, fields, credentials.signature, amountFormatter)
            }
        }

        return fields
    }

    private fun getAuthorizedInvocationFields(
        context: Context,
        fields: MutableList<OperationField>, invocation: SorobanAuthorizedInvocation,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        getAuthorizedFunctionFields(context, fields, invocation.function, amountFormatter)
        invocation.subInvocations.apply {
            if (isNotEmpty()) {
//                fields.add(
//                    OperationField(
//                        context.getString(R.string.op_field_sub_invocations_size),
//                        size.toString()
//                    )
//                )
                forEach {
                    getAuthorizedInvocationFields(context, fields, it, amountFormatter)
                }
            }
        }

        return fields
    }

    private fun getAuthorizedFunctionFields(
        context: Context,
        fields: MutableList<OperationField>, function: SorobanAuthorizedFunction,
        amountFormatter: (value: String) -> String
    ): MutableList<OperationField> {
        when (function) {
            is SorobanAuthorizedFunction.Contract -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_authorized_function),
                        context.getString(R.string.op_value_contract)
                    )
                )

                getInvokeContractArgsFields(context, fields, function.args, amountFormatter)
            }

            is SorobanAuthorizedFunction.CreateContractHost -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_authorized_function),
                        context.getString(R.string.op_value_create_contract_host)
                    )
                )

                getCreateContractArgsFields(context, fields, function.args, amountFormatter)
            }

            is SorobanAuthorizedFunction.CreateContractV2Host -> {
                fields.add(
                    OperationField(
                        context.getString(R.string.op_field_authorized_function),
                        context.getString(R.string.op_value_create_contract_host)
                    )
                )

                getCreateContractArgsV2Fields(context, fields, function.args, amountFormatter)
            }
        }

        return fields
    }

    fun setKeyPostfixFormat(arg: String, arrayArg: String) {
        argPostfixFormat = arg
        arrayArgPostfixFormat = arrayArg
    }
}