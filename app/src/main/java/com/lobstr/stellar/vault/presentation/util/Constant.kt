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
        const val EXTRA_CONFIRM_PIN = "EXTRA_CONFIRM_PIN"
        const val EXTRA_NAVIGATION_FR = "EXTRA_NAVIGATION_FR"
        const val EXTRA_TRANSACTION_ITEM = "EXTRA_TRANSACTION_ITEM"
        const val EXTRA_TRANSACTION_STATUS = "EXTRA_TRANSACTION_STATUS"
        const val EXTRA_ENVELOPE_XDR = "EXTRA_ENVELOPE_XDR"
        const val EXTRA_NEED_ADDITIONAL_SIGNATURES = "EXTRA_NEED_ADDITIONAL_SIGNATURES"
        const val EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE"
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
        const val BUNDLE_ENVELOPE_XDR = "BUNDLE_ENVELOPE_XDR"
        const val BUNDLE_NEED_ADDITIONAL_SIGNATURES = "BUNDLE_NEED_ADDITIONAL_SIGNATURES"
        const val BUNDLE_ERROR_MESSAGE = "BUNDLE_ERROR_MESSAGE"
    }

    object Navigation {
        const val AUTH = 0
        const val DASHBOARD = 1
        const val TRANSACTIONS = 2
        const val SETTINGS = 3
        const val TRANSACTION_DETAILS = 4
        const val MNEMONICS = 5
        const val OPERATION_DETAILS = 6
        const val FINGERPRINT_SET_UP = 7
        const val SIGNER_INFO = 8
        const val SUCCESS = 9
        const val ERROR = 10
        const val IMPORT_XDR = 11
        const val SIGNED_ACCOUNTS = 12
    }

    object Transaction {
        const val PENDING = 1
        const val CANCELLED = 2
        const val SIGNED = 3

        // when transaction created from entered XDR
        const val IMPORT_XDR = 4
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

    object Flavor {
        const val QA = "qa"
        const val VAULT = "vault"
    }

    object Permission {
        const val CAMERA = 1
    }

    object Code {
        const val DETECT_QR_CODE = 101
        const val TRANSACTION_DETAILS_FRAGMENT = 102
        const val OPERATION_DETAILS_FRAGMENT = 103
        const val CHANGE_PIN = 104
        const val CONFIRM_PIN_FOR_MNEMONIC = 105
        const val IMPORT_XDR_FRAGMENT = 106
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
        const val COUNT_MNEMONIC_WORDS_12 = 12
        const val COUNT_MNEMONIC_WORDS_24 = 24
    }

    object ApiRequestTag {
        const val DEFAULT = 0
        const val REFRESH_TOKEN = 1
        const val REFRESH_AUTH = 2
    }

    /**
     * UNKNOWN - the user doesn't see Finger Print setup screen
     * @see com.lobstr.stellar.vault.presentation.auth.touch_id.FingerprintSetUpFragment
     */
    object BiometricState {
        const val UNKNOWN = 0
        const val ENABLED = 1
        const val DISABLED = 2
    }

    /**
     * UNKNOWN - the user doesn't see Rate Us dialog
     * @see com.lobstr.stellar.vault.presentation.home.rate_us.RateUsDialogFragment
     */
    object RateUsState {
        const val UNKNOWN = 0
        const val DEFERRED = 1
        const val SKIPPED = 2
        const val RATED = 3
    }

    object Social {
        const val STORE_URL = "https://play.google.com/store/apps/details?id=com.lobstr.stellar.vault"
    }
}