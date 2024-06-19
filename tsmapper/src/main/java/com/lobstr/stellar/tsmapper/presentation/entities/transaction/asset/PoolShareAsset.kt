package com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class PoolShareAsset(val poolID: String?) : Asset("", "pool_share"),
    Parcelable