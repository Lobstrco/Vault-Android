package com.lobstr.stellar.tsmapper.presentation.entities.transaction


enum class TsMemo(var value: String? = null) {
    MEMO_NONE,
    MEMO_TEXT,
    MEMO_ID,
    MEMO_HASH,
    MEMO_RETURN
}