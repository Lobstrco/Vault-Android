package com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.error

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SCErrorCode(val descr: String) : Parcelable {
    class SCECArithDomain :
        SCErrorCode("some arithmetic wasn't defined\n (overflow, divide-by-zero)")

    class SCECIndexBounds : SCErrorCode("something was indexed beyond its bounds")

    class SCECInvalidInput : SCErrorCode("user provided some otherwise-bad data")

    class SCECMissingValue : SCErrorCode("some value was required but not provided")

    class SCECExistingValue : SCErrorCode("some value was provided where not allowed")

    class SCECExceededLimit : SCErrorCode("some arbitrary limit -- gas or otherwise -- was hit")

    class SCECInvalidAction : SCErrorCode("data was valid but action requested was not")

    class SCECInternalError : SCErrorCode("the internal state of the host was otherwise-bad")

    class SCECUnexpectedType : SCErrorCode("some type wasn't as expected")

    class SCECUnexpectedSize : SCErrorCode("something's size wasn't as expected")
}