package com.lobstr.stellar.tsmapper.presentation.entities.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TsTimeBounds(val minTime: Long = 0, val maxTime: Long = 0) : Parcelable