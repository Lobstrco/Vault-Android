package com.lobstr.stellar.vault.presentation.util

import com.lobstr.stellar.vault.BuildConfig


object Constant {

    object Extra {
        const val EXTRA_PIN_MODE = "EXTRA_PIN_MODE"
        const val EXTRA_NAVIGATION_FR = "EXTRA_NAVIGATION_FR"
        const val EXTRA_TRANSACTION_ITEM = "EXTRA_TRANSACTION_ITEM"
        const val EXTRA_TRANSACTION_STATUS = "EXTRA_TRANSACTION_STATUS"
        const val EXTRA_ENVELOPE_XDR = "EXTRA_ENVELOPE_XDR"
        const val EXTRA_TRANSACTION_CONFIRMATION_SUCCESS_STATUS = "EXTRA_TRANSACTION_CONFIRMATION_SUCCESS_STATUS"
        const val EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE"
        const val EXTRA_CONFIG = "EXTRA_CONFIG"
        const val EXTRA_TANGEM_INFO = "EXTRA_TANGEM_INFO"
        const val EXTRA_NOTIFICATION = "EXTRA_NOTIFICATION"
    }

    object Bundle {
        const val BUNDLE_MNEMONICS_ARRAY = "BUNDLE_MNEMONICS_ARRAY"
        const val BUNDLE_GENERATE_MNEMONICS = "BUNDLE_GENERATE_MNEMONICS"
        const val BUNDLE_PUBLIC_KEY = "BUNDLE_PUBLIC_KEY"
        const val BUNDLE_NAVIGATION_FR = "BUNDLE_NAVIGATION_FR"
        const val BUNDLE_TRANSACTION_ITEM = "BUNDLE_TRANSACTION_ITEM"
        const val BUNDLE_OPERATION_POSITION = "BUNDLE_OPERATION_POSITION"
        const val BUNDLE_ENVELOPE_XDR = "BUNDLE_ENVELOPE_XDR"
        const val BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS =
            "BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS"
        const val BUNDLE_ERROR_MESSAGE = "BUNDLE_ERROR_MESSAGE"
        const val BUNDLE_CONFIG = "BUNDLE_CONFIG"
        const val BUNDLE_PIN_MODE = "BUNDLE_PIN_MODE"
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
    }

    object Transaction {
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
        const val TRANSACTION_DETAILS_FRAGMENT = 102
        const val CHANGE_PIN = 103
        const val CONFIRM_PIN_FOR_MNEMONIC = 104
        const val IMPORT_XDR_FRAGMENT = 105

        /**
         * Config screen identity.
         */
        object Config {
            const val SPAM_PROTECTION = 106
            const val TRANSACTION_CONFIRMATIONS = 107
        }

        const val TANGEM_CREATE_WALLET = 108
    }

    object TransactionType {
        const val PENDING = "pending/"
        const val INACTIVE = "inactive/"

        /**
         * Inner Transaction Type:
         * @see com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem.transactionType
         */
        object Item {
            const val AUTH_CHALLENGE = "auth_challenge"
            const val TRANSACTION = "transaction"
        }
    }

    object Util {
        const val COUNT_MNEMONIC_WORDS_12 = 12
        const val COUNT_MNEMONIC_WORDS_24 = 24
        const val PK_TRUNCATE_COUNT = 8
        const val UNDEFINED_VALUE = -1
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
        const val TRANSACTION = URL.plus("/tx/")
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
        const val SIGNER_CARD_BUY = "https://shop.tangem.com/products/tangem-for-lobstr-vault"
    }

    object TangemErrorMod {
        const val ERROR_MOD_DEFAULT = -1
        const val ERROR_MOD_ONLY_TEXT = 0
        const val ERROR_MOD_FINISH_SCREEN = 1
        const val ERROR_MOD_REPEAT_ACTION = 2
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
        const val LOADED = 2
        const val PURGED = 3
    }

    object PinMode {
        const val CREATE: Byte = 0
        const val CHANGE: Byte = 1
        const val CONFIRM: Byte = 2
        const val ENTER: Byte = 3
    }

    object Support {
        const val RECOVERY_PHRASE = 360013259599L
        const val RECOVER_ACCOUNT = 360013243520L
        const val SIGNING_WITH_VAULT_SIGNER_CARD = 360013838099L
        const val TRANSACTION_CONFIRMATIONS = 360013304739L
    }
}