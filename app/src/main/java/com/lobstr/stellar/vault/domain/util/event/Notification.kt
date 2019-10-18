package com.lobstr.stellar.vault.domain.util.event

data class Notification(val type: Byte, val data: Any?) {
    object Type {
        const val SIGNED_NEW_ACCOUNT: Byte = 0
        const val REMOVED_SIGNER: Byte = 1
        const val ADDED_NEW_TRANSACTION: Byte = 2
        const val TRANSACTION_COUNT_CHANGED: Byte = 3
        const val ADDED_NEW_SIGNATURE: Byte = 4
        const val TRANSACTION_SUBMITTED: Byte = 5
    }
}