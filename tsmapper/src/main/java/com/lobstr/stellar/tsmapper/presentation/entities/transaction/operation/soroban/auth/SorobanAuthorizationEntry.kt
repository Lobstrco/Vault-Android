package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SorobanAuthorizationEntry(
    val credentials: SorobanCredentials,
    val rootInvocation: SorobanAuthorizedInvocation
) : Parcelable