package com.lobstr.stellar.tsmapper.presentation.entities.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class TsMemo(open val value: String?) : Parcelable {
    data class MEMO_NONE(override val value: String?) : TsMemo(value)
    data class MEMO_TEXT(override val value: String?) : TsMemo(value)
    data class MEMO_ID(override val value: String?) : TsMemo(value)
    data class MEMO_HASH(override val value: String?) : TsMemo(value)
    data class MEMO_RETURN(override val value: String?) : TsMemo(value)
}