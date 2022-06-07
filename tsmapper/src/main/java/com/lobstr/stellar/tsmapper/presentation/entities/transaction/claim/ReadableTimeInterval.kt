package com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim.ReadableTimeInterval.Type
import kotlinx.parcelize.Parcelize

/**
 * Readable representation for the [Claimant] Predicates.
 * @param type Interval [Type].
 * @param timeIntervals List of time intervals.
 */
@Parcelize
data class ReadableTimeInterval(val type: Byte, val timeIntervals: MutableList<String?>) :
    Parcelable {
    object Type {
        const val NOT_SET: Byte = -1
        const val BEFORE: Byte = 0
        const val AFTER: Byte = 1
        const val BETWEEN: Byte = 2
        const val NON_CLAIMABLE: Byte = 3
    }
}
