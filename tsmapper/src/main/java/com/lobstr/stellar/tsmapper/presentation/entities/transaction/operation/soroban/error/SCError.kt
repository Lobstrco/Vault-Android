package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.error

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SCError : Parcelable {
    @Parcelize
    sealed class SCErrorWithCode(open val code: SCErrorCode): SCError(), Parcelable

    data class SCEContract(val contractCode: String) : SCError()

    data class SCEWasmVm(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCEContext(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCEStorage(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCEObject(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCECrypto(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCEEvents(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCEBudget(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCEValue(override val code: SCErrorCode) : SCErrorWithCode(code)

    data class SCEAuth(override val code: SCErrorCode) : SCErrorWithCode(code)
}