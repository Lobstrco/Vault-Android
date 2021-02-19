package com.lobstr.stellar.vault.domain.util.event

import com.lobstr.stellar.vault.domain.util.event.Update.Type.COMMON

data class Update(val type: Byte = COMMON) {
    object Type {
        const val COMMON: Byte = 0
        const val AUTH_EVENT_SUCCESS: Byte = 1
    }
}