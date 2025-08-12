package com.lobstr.stellar.vault.presentation.util

import com.lobstr.stellar.vault.BuildConfig


object Constant {

    object Extra {
        const val EXTRA_PIN_MODE = "EXTRA_PIN_MODE"
        const val EXTRA_NAVIGATION_FR = "EXTRA_NAVIGATION_FR"
        const val EXTRA_TRANSACTION_ITEM = "EXTRA_TRANSACTION_ITEM"
        const val EXTRA_USER_ACCOUNT = "EXTRA_USER_ACCOUNT"
        const val EXTRA_TRANSACTION_STATUS = "EXTRA_TRANSACTION_STATUS"
        const val EXTRA_ENVELOPE_XDR = "EXTRA_ENVELOPE_XDR"
        const val EXTRA_TRANSACTION_CONFIRMATION_SUCCESS_STATUS = "EXTRA_TRANSACTION_CONFIRMATION_SUCCESS_STATUS"
        const val EXTRA_ERROR = "EXTRA_ERROR"
        const val EXTRA_CONFIG = "EXTRA_CONFIG"
        const val EXTRA_TANGEM_INFO = "EXTRA_TANGEM_INFO"
        const val EXTRA_TANGEM_OPERATION_TYPE = "EXTRA_TANGEM_OPERATION_TYPE"
        const val EXTRA_PUBLIC_KEY = "EXTRA_PUBLIC_KEY"
    }

    object Bundle {
        const val BUNDLE_MNEMONICS_ARRAY = "BUNDLE_MNEMONICS_ARRAY"
        const val BUNDLE_GENERATE_MNEMONICS = "BUNDLE_GENERATE_MNEMONICS"
        const val BUNDLE_PUBLIC_KEY = "BUNDLE_PUBLIC_KEY"
        const val BUNDLE_NAVIGATION_FR = "BUNDLE_NAVIGATION_FR"
        const val BUNDLE_TRANSACTION_ITEM = "BUNDLE_TRANSACTION_ITEM"
        const val BUNDLE_TRANSACTION_HASH = "BUNDLE_TRANSACTION_HASH"
        const val BUNDLE_OPERATION = "BUNDLE_OPERATION"
        const val BUNDLE_OPERATION_TITLE = "BUNDLE_OPERATION_TITLE"
        const val BUNDLE_OPERATIONS_LIST = "BUNDLE_OPERATIONS_LIST"
        const val BUNDLE_TRANSACTION_TITLE = "BUNDLE_TRANSACTION_TITLE"
        const val BUNDLE_TRANSACTION_SOURCE_ACCOUNT = "BUNDLE_TRANSACTION_SOURCE_ACCOUNT"
        const val BUNDLE_ENVELOPE_XDR = "BUNDLE_ENVELOPE_XDR"
        const val BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS =
            "BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS"
        const val BUNDLE_CONFIG = "BUNDLE_CONFIG"
        const val BUNDLE_PIN_MODE = "BUNDLE_PIN_MODE"
        const val BUNDLE_MANAGE_ACCOUNT_NAME = "BUNDLE_MANAGE_ACCOUNT_NAME"
        const val BUNDLE_SHOW_NETWORK_EXPLORER = "BUNDLE_SHOW_NETWORK_EXPLORER"
        const val BUNDLE_ASSET_CODE = "BUNDLE_ASSET_CODE"
        const val BUNDLE_ASSET_ISSUER = "BUNDLE_ASSET_ISSUER"
        const val BUNDLE_ERROR = "BUNDLE_ERROR"
        const val BUNDLE_CONTRACT_ID = "BUNDLE_CONTRACT_ID"
        const val BUNDLE_TRANSACTIONS_STATUS = "BUNDLE_TRANSACTIONS_STATUS"
        const val BUNDLE_SHOW_COMMON_TOOLBAR = "BUNDLE_SHOW_COMMON_TOOLBAR"
    }

    object Navigation {
        const val AUTH = 0
        const val DASHBOARD = 1
        const val TRANSACTIONS = 2
        const val SETTINGS = 3
        const val TRANSACTION_DETAILS = 4
        const val MNEMONICS = 5
        const val BIOMETRIC_SET_UP = 6
        const val SIGNER_INFO = 7
        const val SUCCESS = 8
        const val ERROR = 9
        const val IMPORT_XDR = 10
        const val SIGNED_ACCOUNTS = 11
        const val CONFIG = 12
        const val VAULT_AUTH = 13
        const val ADD_ACCOUNT_NAME = 14
    }

    object Transaction {
        object Status {
            const val PENDING = "pending"
            const val CANCELLED = "cancelled"
            const val SIGNED = "signed"
        }

        const val PENDING = 1
        const val CANCELLED = 2
        const val SIGNED = 3
        // When transaction created from entered XDR.
        const val IMPORT_XDR = 4
    }

    object TransactionConfirmationSuccessStatus {
        const val SUCCESS: Byte = 0 // Common success status.
        const val SUCCESS_NEED_ADDITIONAL_SIGNATURES: Byte = 1
        const val SUCCESS_CHALLENGE: Byte = 2
    }

    object ConfigType {
        const val NO: Byte = 0
        const val YES: Byte = 1
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
    }

    object BuildType {
        const val RELEASE = "release"
        const val DEBUG = "debug"
    }

    object Flavor {
        const val QA = "qa"
        const val VAULT = "vault"
    }

    object Behavior {
        const val STAGING = "staging"
        const val PRODUCTION = "production"
    }

    object Code {
        /**
         * Config screen identity.
         */
        object Config {
            const val SPAM_PROTECTION = 102
            const val TRANSACTION_CONFIRMATIONS = 103
        }
    }

    object RequestKey{
        const val TRANSACTION_DETAILS_FRAGMENT = "TRANSACTION_DETAILS_FRAGMENT"
    }

    object TransactionType {
        const val PENDING = "pending/"
        const val INACTIVE = "inactive/"
    }

    object Util {
        const val COUNT_MNEMONIC_WORDS_12 = 12
        const val COUNT_MNEMONIC_WORDS_24 = 24
        const val PK_TRUNCATE_COUNT = 8
        const val PK_TRUNCATE_COUNT_SHORT = 4
        const val UNDEFINED_VALUE = -1
        const val PUBLIC_KEY_LIMIT = 20
    }

    object ApiRequestTag {
        const val DEFAULT = 0
        const val REFRESH_TOKEN = 1
        const val REFRESH_AUTH = 2
    }

    /**
     * UNKNOWN - the user doesn't see Biometric setup screen.
     * @see com.lobstr.stellar.vault.presentation.auth.biometric.BiometricSetUpFragment
     */
    object BiometricState {
        const val UNKNOWN = 0
        const val ENABLED = 1
        const val DISABLED = 2
    }

    /**
     * UNKNOWN - the user doesn't see Rate Us dialog.
     * @see com.lobstr.stellar.vault.presentation.home.rate_us.RateUsDialogFragment
     */
    object RateUsState {
        const val UNKNOWN = 0
        const val DEFERRED = 1
        const val SKIPPED = 2
        const val RATED = 3
    }

    object Explorer {
        private const val URL = "https://stellar.expert/explorer/public"
        const val ACCOUNT = URL.plus("/account/")
        const val ASSET = URL.plus("/asset/")
        const val TRANSACTION = URL.plus("/tx/")
        const val CONTRACT = URL.plus("/contract/")
        const val QUERY_FILTER = "filter=%s"
        const val VALUE_INTERFACE ="interface"
    }

    object Laboratory {
        const val URL = "https://lab.stellar.org/"
        const val XDR_VIEW = "xdr/view"
        const val HORIZON_QUERY = "?\$=network\$id=%s&label=%s&horizonUrl=%s&rpcUrl=%s&passphrase=%s;&transaction\$build\$operations@\$operation_type=&params@;;;;;"
        const val XDR_QUERY = "&xdr\$blob=%s&type=%s;;"

        object NETWORK {
            const val MAINNET = "mainnet"
            const val TESTNET = "testnet"
            const val FUTURENET = "futurenet"
            const val CUSTOM = "custom"
        }

        object Type {
            const val TRANSACTION_ENVELOPE = "TransactionEnvelope"
        }
    }

    object LobstrWallet {
        val PACKAGE_NAME =
            if (BuildConfig.FLAVOR.equals(Flavor.VAULT)) "com.lobstr.client" else "com.lobstr.staging"
        const val DEEP_LINK_MULTISIG_SETUP = "lobstr_app://wallet/multisignature/setup"
    }

    object Social {
        const val STORE_URL = "https://play.google.com/store/apps/details?id="
        const val USER_ICON_LINK = "https://id.lobstr.co/"
        const val SUPPORT_MAIL = "support@lobstr.co"
        const val SIGNER_CARD_INFO = "https://vault.lobstr.co/card"
    }

    object TangemErrorMod {
        const val ERROR_MOD_DEFAULT = -1
        const val ERROR_MOD_ONLY_TEXT = 0
        const val ERROR_MOD_FINISH_SCREEN = 1
        const val ERROR_MOD_REPEAT_ACTION = 2
        const val ERROR_MOD_USER_CANCELLED = 3
    }

    object TangemAction {
        const val ACTION_DEFAULT = 0
        const val ACTION_SCAN = 1
        const val ACTION_SIGN = 2
        const val ACTION_CREATE_WALLET = 3
    }

    object TangemCardStatus {
        const val NOT_PERSONALIZED = 0
        const val EMPTY = 1
        const val WALLET_CREATED = 2
        const val PURGED = 3
    }

    object PinMode {
        const val CREATE: Byte = 0
        const val CHANGE: Byte = 1
        const val CONFIRM: Byte = 2
        const val ENTER: Byte = 3
    }

    object RateUsSessionState {
        const val UNDEFINED: Byte = 0
        const val CHECK: Byte = 1
        const val CHECKED: Byte = 2
    }

    object Counter {
        const val APP_UPDATE = 6
        const val START = 0
    }
}