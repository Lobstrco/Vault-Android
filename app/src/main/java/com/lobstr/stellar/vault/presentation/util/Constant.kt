package com.lobstr.stellar.vault.presentation.util


object Constant {

    object Argument {
        const val ARGUMENT_USER_ID = "ARGUMENT_USER_ID"
    }

    object Extra {
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_QR_CODE_RESULT = "EXTRA_QR_CODE_RESULT"
        const val EXTRA_SECRET_KEY = "EXTRA_SECRET_KEY"
    }

    object Bundle {
        const val BUNDLE_SAVED_TOOLBAR_TITLE = "BUNDLE_SAVED_TOOLBAR_TITLE"
        const val BUNDLE_TRANSACTION_HASH = "BUNDLE_TRANSACTION_HASH"
        const val BUNDLE_MNEMONICS_ARRAY = "BUNDLE_MNEMONICS_ARRAY"
        const val BUNDLE_SECRET_KEY = "BUNDLE_SECRET_KEY"
    }

    object Symbol {
        const val ASTERISK = "*"
        const val NEW_LINE = "\n"
        const val SPACE = " "
        const val NULL = ""
        const val COLON = ":"
        const val COMMA = ","
        const val SLASH = "/"
        const val DOT = "."
        const val BULLET = "\u2022"
        const val UNDERLINE = "_"
        const val DASH = "-"
        const val PERCENT = "%"
        const val SPACE_DASH = " - "
        const val AT = "@"
        const val AMPERSAND = "&"
        const val REMOVE = "r_s"
        const val EMPTY_CELL = "e_c"
    }

    object BuildType {
        const val RELEASE = "release"
        const val DEBUG = "debug"
    }

    object Permission {
        const val CAMERA = 1
    }

    object Code {
        const val DETECT_QR_CODE = 101
    }

    object ResultCode {
        const val CANCELED = 0
        const val OK = -1
    }

    object TransactionType {
        const val PENDING = "pending/"
        const val INACTIVE = "inactive/"
    }

    object Util {
        const val COUNT_MNEMONIC_WORDS = 12
    }
}