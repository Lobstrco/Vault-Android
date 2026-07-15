package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCAddress
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.other.SCVal
import kotlinx.parcelize.Parcelize

// CAP-71 (Protocol 27): a single delegate's signature inside an ADDRESS_WITH_DELEGATES
// credential. Delegates form a recursive tree via [nestedDelegates].
@Parcelize
data class SorobanDelegateSignature(
    val address: SCAddress,
    val signature: SCVal,
    val nestedDelegates: List<SorobanDelegateSignature>
) : Parcelable
