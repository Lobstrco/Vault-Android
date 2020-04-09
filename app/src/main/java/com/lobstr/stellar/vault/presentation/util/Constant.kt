package com.lobstr.stellar.vault.presentation.util

import com.lobstr.stellar.vault.BuildConfig


object Constant {

    object Extra {
        const val EXTRA_CREATE_PIN = "EXTRA_CREATE_PIN"
        const val EXTRA_CHANGE_PIN = "EXTRA_CHANGE_PIN"
        const val EXTRA_CONFIRM_PIN = "EXTRA_CONFIRM_PIN"
        const val EXTRA_NAVIGATION_FR = "EXTRA_NAVIGATION_FR"
        const val EXTRA_TRANSACTION_ITEM = "EXTRA_TRANSACTION_ITEM"
        const val EXTRA_TRANSACTION_STATUS = "EXTRA_TRANSACTION_STATUS"
        const val EXTRA_ENVELOPE_XDR = "EXTRA_ENVELOPE_XDR"
        const val EXTRA_NEED_ADDITIONAL_SIGNATURES = "EXTRA_NEED_ADDITIONAL_SIGNATURES"
        const val EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE"
        const val EXTRA_CONFIG = "EXTRA_CONFIG"
    }

    object Bundle {
        const val BUNDLE_MNEMONICS_ARRAY = "BUNDLE_MNEMONICS_ARRAY"
        const val BUNDLE_GENERATE_MNEMONICS = "BUNDLE_GENERATE_MNEMONICS"
        const val BUNDLE_PUBLIC_KEY = "BUNDLE_PUBLIC_KEY"
        const val BUNDLE_NAVIGATION_FR = "BUNDLE_NAVIGATION_FR"
        const val BUNDLE_TRANSACTION_ITEM = "BUNDLE_TRANSACTION_ITEM"
        const val BUNDLE_OPERATION_POSITION = "BUNDLE_OPERATION_POSITION"
        const val BUNDLE_ENVELOPE_XDR = "BUNDLE_ENVELOPE_XDR"
        const val BUNDLE_NEED_ADDITIONAL_SIGNATURES = "BUNDLE_NEED_ADDITIONAL_SIGNATURES"
        const val BUNDLE_ERROR_MESSAGE = "BUNDLE_ERROR_MESSAGE"
        const val BUNDLE_CONFIG = "BUNDLE_CONFIG"
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
    }

    object Transaction {
        const val PENDING = 1
        const val CANCELLED = 2
        const val SIGNED = 3

        // When transaction created from entered XDR.
        const val IMPORT_XDR = 4
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

    object Code {
        const val TRANSACTION_DETAILS_FRAGMENT = 102
        const val OPERATION_DETAILS_FRAGMENT = 103
        const val CHANGE_PIN = 104
        const val CONFIRM_PIN_FOR_MNEMONIC = 105
        const val IMPORT_XDR_FRAGMENT = 106

        /**
         * Config screen identity.
         */
        object Config {
            const val SPAM_PROTECTION = 107
            const val TRANSACTION_CONFIRMATIONS = 108
        }
    }

    object TransactionType {
        const val PENDING = "pending/"
        const val INACTIVE = "inactive/"
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
        const val DEEP_LINK_MULTISIG_SETUP = "lobstr://wallet/multisignature/setup"
    }

    object Social {
        const val STORE_URL = "https://play.google.com/store/apps/details?id="
        const val USER_ICON_LINK = "https://id.lobstr.co/"
    }
}