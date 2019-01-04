package com.lobstr.stellar.vault.presentation.util


object Constant {

    object Argument {
        const val ARGUMENT_USER_ID = "ARGUMENT_USER_ID"
    }

    object Extra {
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_QR_CODE_RESULT = "EXTRA_QR_CODE_RESULT"
        const val EXTRA_CREATE_PIN = "EXTRA_CREATE_PIN"
        const val EXTRA_CHANGE_PIN = "EXTRA_CHANGE_PIN"
        const val EXTRA_NAVIGATION_FR = "EXTRA_NAVIGATION_FR"
        const val EXTRA_TRANSACTION_ITEM = "EXTRA_TRANSACTION_ITEM"
    }

    object Bundle {
        const val BUNDLE_SAVED_TOOLBAR_TITLE = "BUNDLE_SAVED_TOOLBAR_TITLE"
        const val BUNDLE_TRANSACTION_HASH = "BUNDLE_TRANSACTION_HASH"
        const val BUNDLE_MNEMONICS_ARRAY = "BUNDLE_MNEMONICS_ARRAY"
        const val BUNDLE_GENERATE_MNEMONICS = "BUNDLE_GENERATE_MNEMONICS"
        const val BUNDLE_SECRET_KEY = "BUNDLE_SECRET_KEY"
        const val BUNDLE_PUBLIC_KEY = "BUNDLE_PUBLIC_KEY"
        const val BUNDLE_NAVIGATION_FR = "BUNDLE_NAVIGATION_FR"
        const val BUNDLE_TRANSACTION_ITEM = "BUNDLE_TRANSACTION_ITEM"
        const val BUNDLE_OPERATION_POSITION = "BUNDLE_OPERATION_POSITION"
    }

    object Navigation {
        const val AUTH = 0
        const val DASHBOARD = 1
        const val TRANSACTIONS = 2
        const val SETTINGS = 3
        const val TRANSACTION_DETAILS = 4
        const val MNEMONICS = 5
        const val OPERATION_DETAILS = 6
    }

    object Transaction {
        const val PENDING = 1
        const val CANCELLED = 2
        const val SIGNED = 3
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
        const val TRANSACTION_DETAILS_FRAGMENT = 102
        const val OPERATION_DETAILS_FRAGMENT = 103
        const val CHANGE_PIN = 104
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

    object ApiRequestTag {
        const val DEFAULT = 0
        const val REFRESH_TOKEN = 1
        const val REFRESH_AUTH = 2
    }
}