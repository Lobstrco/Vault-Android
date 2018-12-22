package com.lobstr.stellar.vault.domain.util.event


data class Network(val type: Byte) {
    object Type {
        val CONNECTED: Byte = 0
        val DISCONNECTED: Byte = 1
    }
}