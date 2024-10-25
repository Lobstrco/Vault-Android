package com.lobstr.stellar.tsmapper.presentation.util

object Constant {

    object XLM {
        const val CODE = "XLM"
        const val TYPE = "native"
        // 1 stroop = 0.0000001 XLM.
        const val STROOP = "0.0000001"
        const val BASE_FEE = "0.00001"
    }

    object Util {
        const val PK_TRUNCATE_COUNT = 8
        const val UNDEFINED_VALUE = -1
    }

    object TransactionType {
        /**
         * Inner Transaction Type:
         */
        const val AUTH_CHALLENGE = "auth_challenge"
        const val TRANSACTION = "transaction"
    }
}