package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SorobanAuthorizedInvocation(
    val function: SorobanAuthorizedFunction,
    val subInvocations: List<SorobanAuthorizedInvocation>
) : Parcelable