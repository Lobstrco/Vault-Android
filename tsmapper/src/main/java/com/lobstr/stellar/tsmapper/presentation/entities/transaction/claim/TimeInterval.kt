package com.lobstr.stellar.tsmapper.presentation.entities.transaction.claim

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeInterval(
    var type: Int = 0,
    var timeStart: String? = null,
    var timeEnd: String? = null
) : Parcelable {
    object IntervalType {
        const val NORMAL = 0
        const val UNCONDITIONAL = 1
        const val LOCKED = 2
    }
}