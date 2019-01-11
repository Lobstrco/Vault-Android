package com.lobstr.stellar.vault.domain.util.event

import java.util.*


data class Network(val type: Byte, val id: UUID) {
    object Type {
        val CONNECTED: Byte = 0
        val DISCONNECTED: Byte = 1
    }
}