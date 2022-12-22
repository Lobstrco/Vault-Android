package com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation

import android.content.Context
import android.os.Parcelable
import com.lobstr.stellar.tsmapper.R
import kotlinx.parcelize.Parcelize
import org.stellar.sdk.xdr.*

@Parcelize
sealed class OpResultCode(open var code: String, open var message: String) : Parcelable {
    object Code {
        // Main
        const val OP_INNER = "op_inner"
        const val OP_BAD_AUTH = "op_bad_auth"
        const val OP_NO_SOURCE_ACCOUNT = "op_no_source_account"
        const val OP_NOT_SUPPORTED = "op_not_supported"
        const val OP_TOO_MANY_SUBENTRIES = "op_too_many_subentries"
        const val OP_EXCEEDED_WORK_LIMIT = "op_exceeded_work_limit"
        const val OP_TOO_MANY_SPONSORING = "op_too_many_sponsoring"
        const val OP_UNDEFINED = "op_undefined"

        const val OP_SUCCESS = "op_success"
        const val OP_MALFORMED = "op_malformed"

        // Change Trust.
        const val OP_NO_ISSUER = "op_no_issuer"
        const val OP_INVALID_LIMIT = "op_invalid_limit"
        const val OP_LOW_RESERVE = "op_low_reserve"
        const val OP_SELF_NOT_ALLOWED = "op_self_not_allowed"
        const val OP_TRUST_LINE_MISSING = "op_trust_line_missing"
        const val OP_CANNOT_DELETE = "op_cannot_delete"
        const val OP_NOT_AUTH_MAINTAIN_LIABILITIES = "op_not_auth_maintain_liabilities" // NOTE op_not_aut_maintain_liabilities (GO code).

        // Account Merge
        const val OP_IMMUTABLE_SET = "op_immutable_set"
        const val OP_HAS_SUB_ENTRIES = "op_has_sub_entries"
        const val OP_NO_ACCOUNT = "op_no_account"
        const val OP_SEQNUM_TOO_FAR = "op_seqnum_to_far" // NOTE op_seq_num_too_far (GO code).
        const val OP_DEST_FULL = "op_dest_full"
        const val OP_IS_SPONSOR = "op_is_sponsor"

        // Create Account
        const val OP_UNDERFUNDED = "op_underfunded"
        const val OP_ALREADY_EXIST = "op_already_exists"

        // Payment
        const val OP_SRC_NO_TRUST = "op_src_no_trust"
        const val OP_SRC_NOT_AUTHORIZED = "op_src_not_authorized"
        const val OP_NO_DESTINATION = "op_no_destination"
        const val OP_NO_TRUST = "op_no_trust"
        const val OP_NOT_AUTHORIZED = "op_not_authorized"
        const val OP_LINE_FULL = "op_line_full"

        // Path Payment Strict Receive
        const val OP_TOO_FEW_OFFERS = "op_too_few_offers"
        const val OP_OVER_SENDMAX = "op_over_source_max"

        // Path Payment Strict Send
        const val OP_UNDER_DESTMIN = "op_under_dest_min"

        // Manage Sell Offer
        const val SELL_NOT_AUTHORIZED = "sell_not_authorized"
        const val BUY_NOT_AUTHORIZED = "buy_not_authorized"
        const val OP_CROSS_SELF = "op_cross_self"
        const val OP_SELL_NO_ISSUER = "op_sell_no_issuer"
        const val OP_BUY_NO_ISSUER = "op_buy_no_issuer"
        const val OP_OFFER_NOT_FOUND = "op_offer_not_found"

        // Manage Buy Offer
        const val OP_SELL_NO_TRUST = "op_sell_no_trust"
        const val OP_BUY_NO_TRUST = "op_buy_no_trust"

        // Set Options
        const val OP_TOO_MANY_SIGNERS = "op_too_many_signers"
        const val OP_BAD_FLAGS = "op_bad_flags"
        const val OP_INVALID_INFLATION = "op_invalid_inflation"
        const val OP_CANT_CHANGE = "op_cant_change"
        const val OP_UNKNOWN_FLAG = "op_unknown_flag"
        const val OP_THRESHOLD_OUT_OF_RANGE = "op_threshold_out_of_range"
        const val OP_BAD_SIGNER = "op_bad_signer"
        const val OP_INVALID_HOME_DOMAIN = "op_invalid_home_domain"
        const val OP_AUTH_REVOCABLE_REQUIRED = "op_auth_revocable_required"

        // Allow Trust
        const val OP_NO_TRUST_LINE = "op_no_trustline" // NOTE op_no_trust (GO using here OP_NO_TRUST code).
        const val OP_NOT_REQUIRED = "op_not_required"
        const val OP_CANT_REVOKE = "op_cant_revoke"

        // Manage Data
        const val OP_NOT_SUPPORTED_YET = "op_not_supported_yet"
        const val OP_DATA_NAME_NOT_FOUND = "op_data_name_not_found"
        const val OP_DATA_INVALID_NAME = "op_data_invalid_name"

        // Claim Claimable Balance
        const val OP_DOES_NOT_EXIST = "op_does_not_exist"
        const val OP_CANNOT_CLAIM = "op_cannot_claim"

        // Inflation
        const val OP_NOT_TIME = "op_not_time"

        // Bump Sequence
        const val OP_BAD_SEQ = "op_bad_seq"

        // Begin Sponsoring Future Reserves
        const val OP_ALREADY_SPONSORED = "op_already_sponsored"
        const val OP_RECURSIVE = "op_recursive"

        // End Sponsoring Future Reserves
        const val OP_NOT_SPONSORED = "op_not_sponsored"

        // Revoke Sponsorship
        const val OP_NOT_SPONSOR = "op_not_sponsor"
        const val OP_ONLY_TRANSFERABLE = "op_only_transferable"

        // Clawback
        const val OP_NOT_CLAWBACK_ENABLED = "op_not_clawback_enabled"

        // Clawback Claimable Balance
        const val OP_NOT_ISSUER = "op_not_issuer" // NOTE OP_NO_ISSUER (GO code).

        // Set Trust Line Flags
        const val OP_INVALID_STATE = "op_invalid_state"

        // Liquidity Pool Deposit
        const val OP_BAD_PRICE = "op_bad_price"
        const val OP_POOL_FULL = "op_pool_full"

        // Liquidity Pool Withdraw
        const val OP_UNDER_MINIMUM = "op_under_minimum"
    }

    @Parcelize
    data class TsDefaultResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: OperationResultCode?): TsDefaultResultCode =
                when (code) {
                    OperationResultCode.opINNER -> TsDefaultResultCode(
                        Code.OP_INNER,
                        c.getString(R.string.op_inner)
                    )
                    OperationResultCode.opBAD_AUTH -> TsDefaultResultCode(
                        Code.OP_BAD_AUTH,
                        c.getString(R.string.op_bad_auth)
                    )
                    OperationResultCode.opNO_ACCOUNT -> TsDefaultResultCode(
                        Code.OP_NO_SOURCE_ACCOUNT,
                        c.getString(R.string.op_no_account)
                    )
                    OperationResultCode.opNOT_SUPPORTED -> TsDefaultResultCode(
                        Code.OP_NOT_SUPPORTED,
                        c.getString(R.string.op_not_supported)
                    )
                    OperationResultCode.opTOO_MANY_SUBENTRIES -> TsDefaultResultCode(
                        Code.OP_TOO_MANY_SUBENTRIES,
                        c.getString(R.string.op_to_many_subentries)
                    )
                    OperationResultCode.opEXCEEDED_WORK_LIMIT -> TsDefaultResultCode(
                        Code.OP_EXCEEDED_WORK_LIMIT,
                        c.getString(R.string.op_exceed_work_limit)
                    )
                    OperationResultCode.opTOO_MANY_SPONSORING -> TsDefaultResultCode(
                        Code.OP_TOO_MANY_SPONSORING,
                        c.getString(R.string.op_too_many_sponsoring)
                    )
                    else -> TsDefaultResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }

            fun create(c: Context, code: String?): TsDefaultResultCode =
                when (code) {
                    Code.OP_INNER -> TsDefaultResultCode(
                        Code.OP_INNER,
                        c.getString(R.string.op_inner)
                    )
                    Code.OP_BAD_AUTH -> TsDefaultResultCode(
                        Code.OP_BAD_AUTH,
                        c.getString(R.string.op_bad_auth)
                    )
                    Code.OP_NO_SOURCE_ACCOUNT -> TsDefaultResultCode(
                        Code.OP_NO_SOURCE_ACCOUNT,
                        c.getString(R.string.op_no_account)
                    )
                    Code.OP_NOT_SUPPORTED -> TsDefaultResultCode(
                        Code.OP_NOT_SUPPORTED,
                        c.getString(R.string.op_not_supported)
                    )
                    Code.OP_TOO_MANY_SUBENTRIES -> TsDefaultResultCode(
                        Code.OP_TOO_MANY_SUBENTRIES,
                        c.getString(R.string.op_to_many_subentries)
                    )
                    Code.OP_EXCEEDED_WORK_LIMIT -> TsDefaultResultCode(
                        Code.OP_EXCEEDED_WORK_LIMIT,
                        c.getString(R.string.op_exceed_work_limit)
                    )
                    Code.OP_TOO_MANY_SPONSORING -> TsDefaultResultCode(
                        Code.OP_TOO_MANY_SPONSORING,
                        c.getString(R.string.op_too_many_sponsoring)
                    )
                    else -> TsDefaultResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsCreateAccountResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: CreateAccountResultCode): TsCreateAccountResultCode =
                when (code) {
                    CreateAccountResultCode.CREATE_ACCOUNT_SUCCESS -> TsCreateAccountResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    CreateAccountResultCode.CREATE_ACCOUNT_MALFORMED -> TsCreateAccountResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.create_account_malformed)
                    )
                    CreateAccountResultCode.CREATE_ACCOUNT_UNDERFUNDED -> TsCreateAccountResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.create_account_underfunded)
                    )
                    CreateAccountResultCode.CREATE_ACCOUNT_LOW_RESERVE -> TsCreateAccountResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.create_account_low_reserve)
                    )
                    CreateAccountResultCode.CREATE_ACCOUNT_ALREADY_EXIST -> TsCreateAccountResultCode(
                        Code.OP_ALREADY_EXIST,
                        c.getString(R.string.create_account_already_exist)
                    )
                    else -> TsCreateAccountResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsPaymentResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: PaymentResultCode): TsPaymentResultCode =
                when (code) {
                    PaymentResultCode.PAYMENT_SUCCESS -> TsPaymentResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    PaymentResultCode.PAYMENT_MALFORMED -> TsPaymentResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.payment_malformed)
                    )

                    PaymentResultCode.PAYMENT_UNDERFUNDED -> TsPaymentResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.payment_underfunded)
                    )
                    PaymentResultCode.PAYMENT_SRC_NO_TRUST -> TsPaymentResultCode(
                        Code.OP_SRC_NO_TRUST,
                        c.getString(R.string.payment_src_no_trust)
                    )
                    PaymentResultCode.PAYMENT_SRC_NOT_AUTHORIZED -> TsPaymentResultCode(
                        Code.OP_SRC_NOT_AUTHORIZED,
                        c.getString(R.string.payment_src_not_authorized)
                    )
                    PaymentResultCode.PAYMENT_NO_DESTINATION -> TsPaymentResultCode(
                        Code.OP_NO_DESTINATION,
                        c.getString(R.string.payment_no_destination)
                    )
                    PaymentResultCode.PAYMENT_NO_TRUST -> TsPaymentResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.payment_no_trust)
                    )
                    PaymentResultCode.PAYMENT_NOT_AUTHORIZED -> TsPaymentResultCode(
                        Code.OP_NOT_AUTHORIZED,
                        c.getString(R.string.payment_not_authorized)
                    )
                    PaymentResultCode.PAYMENT_LINE_FULL -> TsPaymentResultCode(
                        Code.OP_LINE_FULL,
                        c.getString(R.string.payment_line_full)
                    )
                    PaymentResultCode.PAYMENT_NO_ISSUER -> TsPaymentResultCode(
                        Code.OP_NO_ISSUER,
                        c.getString(R.string.payment_no_issuer)
                    )
                    else -> TsPaymentResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsPathPaymentStrictReceiveResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: PathPaymentStrictReceiveResultCode): TsPathPaymentStrictReceiveResultCode =
                when (code) {
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_SUCCESS -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_MALFORMED -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.path_payment_strict_malformed)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_UNDERFUNDED -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.path_payment_strict_underfunded)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_SRC_NO_TRUST -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_SRC_NO_TRUST,
                        c.getString(R.string.path_payment_strict_src_no_trust)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_SRC_NOT_AUTHORIZED -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_SRC_NOT_AUTHORIZED,
                        c.getString(R.string.path_payment_strict_src_not_authorized)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_NO_DESTINATION -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_NO_DESTINATION,
                        c.getString(R.string.path_payment_strict_no_destination)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_NO_TRUST -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.path_payment_strict_no_trust)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_NOT_AUTHORIZED -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_NOT_AUTHORIZED,
                        c.getString(R.string.path_payment_strict_not_authorized)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_LINE_FULL -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_LINE_FULL,
                        c.getString(R.string.path_payment_strict_line_full)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_NO_ISSUER -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_NO_ISSUER,
                        c.getString(R.string.path_payment_strict_no_issuer)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_TOO_FEW_OFFERS -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_TOO_FEW_OFFERS,
                        c.getString(R.string.path_payment_strict_too_few_offers)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_OFFER_CROSS_SELF -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_CROSS_SELF,
                        c.getString(R.string.path_payment_strict_offer_cross_self)
                    )
                    PathPaymentStrictReceiveResultCode.PATH_PAYMENT_STRICT_RECEIVE_OVER_SENDMAX -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_OVER_SENDMAX,
                        c.getString(R.string.path_payment_strict_receive_over_sendmax)
                    )
                    else -> TsPathPaymentStrictReceiveResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsPathPaymentStrictSendResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: PathPaymentStrictSendResultCode): TsPathPaymentStrictSendResultCode =
                when (code) {
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_SUCCESS -> TsPathPaymentStrictSendResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_MALFORMED -> TsPathPaymentStrictSendResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.path_payment_strict_malformed)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_UNDERFUNDED -> TsPathPaymentStrictSendResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.path_payment_strict_underfunded)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_SRC_NO_TRUST -> TsPathPaymentStrictSendResultCode(
                        Code.OP_SRC_NO_TRUST,
                        c.getString(R.string.path_payment_strict_src_no_trust)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_SRC_NOT_AUTHORIZED -> TsPathPaymentStrictSendResultCode(
                        Code.OP_SRC_NOT_AUTHORIZED,
                        c.getString(R.string.path_payment_strict_src_not_authorized)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_NO_DESTINATION -> TsPathPaymentStrictSendResultCode(
                        Code.OP_NO_DESTINATION,
                        c.getString(R.string.path_payment_strict_no_destination)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_NO_TRUST -> TsPathPaymentStrictSendResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.path_payment_strict_no_trust)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_NOT_AUTHORIZED -> TsPathPaymentStrictSendResultCode(
                        Code.OP_NOT_AUTHORIZED,
                        c.getString(R.string.path_payment_strict_not_authorized)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_LINE_FULL -> TsPathPaymentStrictSendResultCode(
                        Code.OP_LINE_FULL,
                        c.getString(R.string.path_payment_strict_line_full)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_NO_ISSUER -> TsPathPaymentStrictSendResultCode(
                        Code.OP_NO_ISSUER,
                        c.getString(R.string.path_payment_strict_no_issuer)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_TOO_FEW_OFFERS -> TsPathPaymentStrictSendResultCode(
                        Code.OP_TOO_FEW_OFFERS,
                        c.getString(R.string.path_payment_strict_too_few_offers)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_OFFER_CROSS_SELF -> TsPathPaymentStrictSendResultCode(
                        Code.OP_CROSS_SELF,
                        c.getString(R.string.path_payment_strict_offer_cross_self)
                    )
                    PathPaymentStrictSendResultCode.PATH_PAYMENT_STRICT_SEND_UNDER_DESTMIN -> TsPathPaymentStrictSendResultCode(
                        Code.OP_UNDER_DESTMIN,
                        c.getString(R.string.path_payment_strict_send_under_destmin)
                    )
                    else -> TsPathPaymentStrictSendResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    open class TsManageSellOfferResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
            companion object {
                fun create (c: Context, code: ManageSellOfferResultCode) = create(c, code) { tsCode, tsMessage ->
                    TsManageSellOfferResultCode(tsCode, tsMessage)
                }
                fun <T : TsManageSellOfferResultCode> create(c: Context, code: ManageSellOfferResultCode, factory:(code: String, message: String) -> T) : T =
                    when (code) {
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_SUCCESS -> factory(
                            Code.OP_SUCCESS,
                            c.getString(R.string.op_success)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_MALFORMED -> factory(
                            Code.OP_MALFORMED,
                            c.getString(R.string.manage_offer_malformed)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_SELL_NO_TRUST -> factory(
                            Code.OP_SELL_NO_TRUST,
                            c.getString(R.string.manage_offer_sell_no_trust)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_BUY_NO_TRUST -> factory(
                            Code.OP_BUY_NO_TRUST,
                            c.getString(R.string.manage_offer_buy_no_trust)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_SELL_NOT_AUTHORIZED -> factory(
                            Code.SELL_NOT_AUTHORIZED,
                            c.getString(R.string.manage_offer_sell_not_authorized)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_BUY_NOT_AUTHORIZED -> factory(
                            Code.BUY_NOT_AUTHORIZED,
                            c.getString(R.string.manage_offer_buy_not_authorized)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_LINE_FULL -> factory(
                            Code.OP_LINE_FULL,
                            c.getString(R.string.manage_offer_line_full)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_UNDERFUNDED -> factory(
                            Code.OP_UNDERFUNDED,
                            c.getString(R.string.manage_offer_underfunded)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_CROSS_SELF -> factory(
                            Code.OP_CROSS_SELF,
                            c.getString(R.string.manage_offer_cross_self)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_SELL_NO_ISSUER -> factory(
                            Code.OP_SELL_NO_ISSUER,
                            c.getString(R.string.manage_offer_sell_no_issuer)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_BUY_NO_ISSUER -> factory(
                            Code.OP_BUY_NO_ISSUER,
                            c.getString(R.string.manage_offer_buy_no_issuer)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_NOT_FOUND -> factory(
                            Code.OP_OFFER_NOT_FOUND,
                            c.getString(R.string.manage_offer_not_found)
                        )
                        ManageSellOfferResultCode.MANAGE_SELL_OFFER_LOW_RESERVE -> factory(
                            Code.OP_LOW_RESERVE,
                            c.getString(R.string.manage_offer_low_reserve)
                        )
                        else -> factory(
                            Code.OP_UNDEFINED,
                            c.getString(R.string.op_undefined)
                        )
                    }
            }
    }

    @Parcelize
    data class TsCreatePassiveSellOfferResultCode(override var code: String, override var message: String) :
        TsManageSellOfferResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: ManageSellOfferResultCode) : TsCreatePassiveSellOfferResultCode  {
                return create(c, code) { tsCode, tsMessage -> TsCreatePassiveSellOfferResultCode(tsCode, tsMessage) }
            }
        }
    }

    @Parcelize
    data class TsManageBuyOfferResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: ManageBuyOfferResultCode?): TsManageBuyOfferResultCode =
                when (code) {
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_SUCCESS -> TsManageBuyOfferResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_MALFORMED -> TsManageBuyOfferResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.manage_offer_malformed)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_SELL_NO_TRUST -> TsManageBuyOfferResultCode(
                        Code.OP_SELL_NO_TRUST,
                        c.getString(R.string.manage_offer_sell_no_trust)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_BUY_NO_TRUST -> TsManageBuyOfferResultCode(
                        Code.OP_BUY_NO_TRUST,
                        c.getString(R.string.manage_offer_buy_no_trust)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_SELL_NOT_AUTHORIZED -> TsManageBuyOfferResultCode(
                        Code.SELL_NOT_AUTHORIZED,
                        c.getString(R.string.manage_offer_sell_not_authorized)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_BUY_NOT_AUTHORIZED -> TsManageBuyOfferResultCode(
                        Code.BUY_NOT_AUTHORIZED,
                        c.getString(R.string.manage_offer_buy_not_authorized)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_LINE_FULL -> TsManageBuyOfferResultCode(
                        Code.OP_LINE_FULL,
                        c.getString(R.string.manage_offer_line_full)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_UNDERFUNDED -> TsManageBuyOfferResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.manage_offer_underfunded)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_CROSS_SELF -> TsManageBuyOfferResultCode(
                        Code.OP_CROSS_SELF,
                        c.getString(R.string.manage_offer_cross_self)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_SELL_NO_ISSUER -> TsManageBuyOfferResultCode(
                        Code.OP_SELL_NO_ISSUER,
                        c.getString(R.string.manage_offer_sell_no_issuer)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_BUY_NO_ISSUER -> TsManageBuyOfferResultCode(
                        Code.OP_BUY_NO_ISSUER,
                        c.getString(R.string.manage_offer_buy_no_issuer)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_NOT_FOUND -> TsManageBuyOfferResultCode(
                        Code.OP_OFFER_NOT_FOUND,
                        c.getString(R.string.manage_offer_not_found)
                    )
                    ManageBuyOfferResultCode.MANAGE_BUY_OFFER_LOW_RESERVE -> TsManageBuyOfferResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.manage_offer_low_reserve)
                    )
                    else -> TsManageBuyOfferResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsSetOptionsResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: SetOptionsResultCode?): TsSetOptionsResultCode =
                when (code) {
                    SetOptionsResultCode.SET_OPTIONS_SUCCESS -> TsSetOptionsResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    SetOptionsResultCode.SET_OPTIONS_LOW_RESERVE -> TsSetOptionsResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.set_options_low_reserve)
                    )
                    SetOptionsResultCode.SET_OPTIONS_TOO_MANY_SIGNERS -> TsSetOptionsResultCode(
                        Code.OP_TOO_MANY_SIGNERS,
                        c.getString(R.string.set_options_too_many_signers)
                    )
                    SetOptionsResultCode.SET_OPTIONS_BAD_FLAGS -> TsSetOptionsResultCode(
                        Code.OP_BAD_FLAGS,
                        c.getString(R.string.set_options_bad_flags)
                    )
                    SetOptionsResultCode.SET_OPTIONS_INVALID_INFLATION -> TsSetOptionsResultCode(
                        Code.OP_INVALID_INFLATION,
                        c.getString(R.string.set_options_invalid_inflation)
                    )
                    SetOptionsResultCode.SET_OPTIONS_CANT_CHANGE -> TsSetOptionsResultCode(
                        Code.OP_CANT_CHANGE,
                        c.getString(R.string.set_options_cant_change)
                    )
                    SetOptionsResultCode.SET_OPTIONS_UNKNOWN_FLAG -> TsSetOptionsResultCode(
                        Code.OP_UNKNOWN_FLAG,
                        c.getString(R.string.set_options_unknown_flag)
                    )
                    SetOptionsResultCode.SET_OPTIONS_THRESHOLD_OUT_OF_RANGE -> TsSetOptionsResultCode(
                        Code.OP_THRESHOLD_OUT_OF_RANGE,
                        c.getString(R.string.set_options_threshold_out_of_range)
                    )
                    SetOptionsResultCode.SET_OPTIONS_BAD_SIGNER -> TsSetOptionsResultCode(
                        Code.OP_BAD_SIGNER,
                        c.getString(R.string.set_options_bad_signer)
                    )
                    SetOptionsResultCode.SET_OPTIONS_INVALID_HOME_DOMAIN -> TsSetOptionsResultCode(
                        Code.OP_INVALID_HOME_DOMAIN,
                        c.getString(R.string.set_options_invalid_home_domain)
                    )
                    SetOptionsResultCode.SET_OPTIONS_AUTH_REVOCABLE_REQUIRED -> TsSetOptionsResultCode(
                        Code.OP_AUTH_REVOCABLE_REQUIRED,
                        c.getString(R.string.set_options_auth_revocable_required)
                    )
                    else -> TsSetOptionsResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsChangeTrustResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: ChangeTrustResultCode?): TsChangeTrustResultCode =
                when (code) {
                    ChangeTrustResultCode.CHANGE_TRUST_SUCCESS -> TsChangeTrustResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_MALFORMED -> TsChangeTrustResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.change_trust_malformed)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_NO_ISSUER -> TsChangeTrustResultCode(
                        Code.OP_NO_ISSUER,
                        c.getString(R.string.change_trust_no_issuer)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_INVALID_LIMIT -> TsChangeTrustResultCode(
                        Code.OP_INVALID_LIMIT,
                        c.getString(R.string.change_trust_invalid_limit)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_LOW_RESERVE -> TsChangeTrustResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.change_trust_low_reserve)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_SELF_NOT_ALLOWED -> TsChangeTrustResultCode(
                        Code.OP_SELF_NOT_ALLOWED,
                        c.getString(R.string.change_trust_self_not_allowed)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_TRUST_LINE_MISSING -> TsChangeTrustResultCode(
                        Code.OP_TRUST_LINE_MISSING,
                        c.getString(R.string.change_trust_trust_line_missing)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_CANNOT_DELETE -> TsChangeTrustResultCode(
                        Code.OP_CANNOT_DELETE,
                        c.getString(R.string.change_trust_cannot_delete)
                    )
                    ChangeTrustResultCode.CHANGE_TRUST_NOT_AUTH_MAINTAIN_LIABILITIES -> TsChangeTrustResultCode(
                        Code.OP_NOT_AUTH_MAINTAIN_LIABILITIES,
                        c.getString(R.string.change_trust_not_auth_maintain_liabilities)
                    )
                    else -> TsChangeTrustResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsAllowTrustResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: AllowTrustResultCode?): TsAllowTrustResultCode =
                when (code) {
                    AllowTrustResultCode.ALLOW_TRUST_SUCCESS -> TsAllowTrustResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    AllowTrustResultCode.ALLOW_TRUST_MALFORMED -> TsAllowTrustResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.allow_trust_malformed)
                    )
                    AllowTrustResultCode.ALLOW_TRUST_NO_TRUST_LINE -> TsAllowTrustResultCode(
                        Code.OP_NO_TRUST_LINE, // NOTE from GO here OP_NO_TRUST
                        c.getString(R.string.allow_trust_no_trust_line)
                    )
                    AllowTrustResultCode.ALLOW_TRUST_TRUST_NOT_REQUIRED -> TsAllowTrustResultCode(
                        Code.OP_NOT_REQUIRED,
                        c.getString(R.string.allow_trust_trust_not_required)
                    )
                    AllowTrustResultCode.ALLOW_TRUST_CANT_REVOKE -> TsAllowTrustResultCode(
                        Code.OP_CANT_REVOKE,
                        c.getString(R.string.allow_trust_cant_revoke)
                    )
                    AllowTrustResultCode.ALLOW_TRUST_SELF_NOT_ALLOWED -> TsAllowTrustResultCode(
                        Code.OP_SELF_NOT_ALLOWED,
                        c.getString(R.string.allow_trust_self_not_allowed)
                    )
                    AllowTrustResultCode.ALLOW_TRUST_LOW_RESERVE -> TsAllowTrustResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.allow_trust_low_reserve)
                    )
                    else -> TsAllowTrustResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsAccountMergeResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: AccountMergeResultCode?): TsAccountMergeResultCode =
                when (code) {
                    AccountMergeResultCode.ACCOUNT_MERGE_SUCCESS -> TsAccountMergeResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    AccountMergeResultCode.ACCOUNT_MERGE_MALFORMED -> TsAccountMergeResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.account_merge_malformed)
                    )
                    AccountMergeResultCode.ACCOUNT_MERGE_NO_ACCOUNT -> TsAccountMergeResultCode(
                        Code.OP_NO_ACCOUNT,
                        c.getString(R.string.account_merge_no_account)
                    )
                    AccountMergeResultCode.ACCOUNT_MERGE_IMMUTABLE_SET -> TsAccountMergeResultCode(
                        Code.OP_IMMUTABLE_SET,
                        c.getString(R.string.account_merge_immutable_set)
                    )
                    AccountMergeResultCode.ACCOUNT_MERGE_HAS_SUB_ENTRIES -> TsAccountMergeResultCode(
                        Code.OP_HAS_SUB_ENTRIES,
                        c.getString(R.string.account_merge_has_sub_entries)
                    )
                    AccountMergeResultCode.ACCOUNT_MERGE_SEQNUM_TOO_FAR -> TsAccountMergeResultCode(
                        Code.OP_SEQNUM_TOO_FAR,
                        c.getString(R.string.account_merge_seqnum_to_far)
                    )
                    AccountMergeResultCode.ACCOUNT_MERGE_DEST_FULL -> TsAccountMergeResultCode(
                        Code.OP_DEST_FULL,
                        c.getString(R.string.account_merge_dest_full)
                    )
                    AccountMergeResultCode.ACCOUNT_MERGE_IS_SPONSOR -> TsAccountMergeResultCode(
                        Code.OP_IS_SPONSOR,
                        c.getString(R.string.account_merge_is_sponsor)
                    )
                    else -> TsAccountMergeResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsManageDataResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: ManageDataResultCode?): TsManageDataResultCode =
                when (code) {
                    ManageDataResultCode.MANAGE_DATA_SUCCESS -> TsManageDataResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    ManageDataResultCode.MANAGE_DATA_NOT_SUPPORTED_YET -> TsManageDataResultCode(
                        Code.OP_NOT_SUPPORTED_YET,
                        c.getString(R.string.manage_data_not_supported_yet)
                    )
                    ManageDataResultCode.MANAGE_DATA_NAME_NOT_FOUND -> TsManageDataResultCode(
                        Code.OP_DATA_NAME_NOT_FOUND,
                        c.getString(R.string.manage_data_name_not_found)
                    )
                    ManageDataResultCode.MANAGE_DATA_LOW_RESERVE -> TsManageDataResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.manage_data_low_reserve)
                    )
                    ManageDataResultCode.MANAGE_DATA_INVALID_NAME -> TsManageDataResultCode(
                        Code.OP_DATA_INVALID_NAME,
                        c.getString(R.string.manage_data_invalid_name)
                    )
                    else -> TsManageDataResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsClaimClaimableBalanceResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: ClaimClaimableBalanceResultCode?): TsClaimClaimableBalanceResultCode =
                when (code) {
                    ClaimClaimableBalanceResultCode.CLAIM_CLAIMABLE_BALANCE_SUCCESS -> TsClaimClaimableBalanceResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    ClaimClaimableBalanceResultCode.CLAIM_CLAIMABLE_BALANCE_DOES_NOT_EXIST -> TsClaimClaimableBalanceResultCode(
                        Code.OP_DOES_NOT_EXIST,
                        c.getString(R.string.claim_claimable_balance_does_not_exist)
                    )
                    ClaimClaimableBalanceResultCode.CLAIM_CLAIMABLE_BALANCE_CANNOT_CLAIM -> TsClaimClaimableBalanceResultCode(
                        Code.OP_CANNOT_CLAIM,
                        c.getString(R.string.claim_claimable_balance_cannot_claim)
                    )
                    ClaimClaimableBalanceResultCode.CLAIM_CLAIMABLE_BALANCE_LINE_FULL -> TsClaimClaimableBalanceResultCode(
                        Code.OP_LINE_FULL,
                        c.getString(R.string.claim_claimable_balance_line_full)
                    )
                    ClaimClaimableBalanceResultCode.CLAIM_CLAIMABLE_BALANCE_NO_TRUST -> TsClaimClaimableBalanceResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.claim_claimable_balance_no_trust)
                    )
                    ClaimClaimableBalanceResultCode.CLAIM_CLAIMABLE_BALANCE_NOT_AUTHORIZED -> TsClaimClaimableBalanceResultCode(
                        Code.OP_NOT_AUTHORIZED,
                        c.getString(R.string.claim_claimable_balance_not_authorized)
                    )
                    else -> TsClaimClaimableBalanceResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsCreateClaimableBalanceResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: CreateClaimableBalanceResultCode?): TsCreateClaimableBalanceResultCode =
                when (code) {
                    CreateClaimableBalanceResultCode.CREATE_CLAIMABLE_BALANCE_SUCCESS -> TsCreateClaimableBalanceResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    CreateClaimableBalanceResultCode.CREATE_CLAIMABLE_BALANCE_MALFORMED -> TsCreateClaimableBalanceResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.create_claimable_balance_malformed)
                    )
                    CreateClaimableBalanceResultCode.CREATE_CLAIMABLE_BALANCE_LOW_RESERVE -> TsCreateClaimableBalanceResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.create_claimable_balance_low_reserve)
                    )
                    CreateClaimableBalanceResultCode.CREATE_CLAIMABLE_BALANCE_NO_TRUST -> TsCreateClaimableBalanceResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.create_claimable_balance_no_trust)
                    )
                    CreateClaimableBalanceResultCode.CREATE_CLAIMABLE_BALANCE_NOT_AUTHORIZED -> TsCreateClaimableBalanceResultCode(
                        Code.OP_NOT_AUTHORIZED,
                        c.getString(R.string.create_claimable_balance_not_authorized)
                    )
                    CreateClaimableBalanceResultCode.CREATE_CLAIMABLE_BALANCE_UNDERFUNDED -> TsCreateClaimableBalanceResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.create_claimable_balance_underfunded)
                    )
                    else -> TsCreateClaimableBalanceResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsInflationResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: InflationResultCode?): TsInflationResultCode =
                when (code) {
                    InflationResultCode.INFLATION_SUCCESS -> TsInflationResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    InflationResultCode.INFLATION_NOT_TIME -> TsInflationResultCode(
                        Code.OP_NOT_TIME,
                        c.getString(R.string.inflation_not_time)
                    )
                    else -> TsInflationResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsBumpSequenceResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: BumpSequenceResultCode?): TsBumpSequenceResultCode =
                when (code) {
                    BumpSequenceResultCode.BUMP_SEQUENCE_SUCCESS -> TsBumpSequenceResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    BumpSequenceResultCode.BUMP_SEQUENCE_BAD_SEQ -> TsBumpSequenceResultCode(
                        Code.OP_BAD_SEQ,
                        c.getString(R.string.bump_sequnce_bad_seq)
                    )
                    else -> TsBumpSequenceResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsBeginSponsoringFutureReservesResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: BeginSponsoringFutureReservesResultCode?): TsBeginSponsoringFutureReservesResultCode =
                when (code) {
                    BeginSponsoringFutureReservesResultCode.BEGIN_SPONSORING_FUTURE_RESERVES_SUCCESS -> TsBeginSponsoringFutureReservesResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    BeginSponsoringFutureReservesResultCode.BEGIN_SPONSORING_FUTURE_RESERVES_MALFORMED -> TsBeginSponsoringFutureReservesResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.begin_sponsoring_future_reserves_malformed)
                    )
                    BeginSponsoringFutureReservesResultCode.BEGIN_SPONSORING_FUTURE_RESERVES_ALREADY_SPONSORED -> TsBeginSponsoringFutureReservesResultCode(
                        Code.OP_ALREADY_SPONSORED,
                        c.getString(R.string.begin_sponsoring_future_reserves_already_sponsored)
                    )
                    BeginSponsoringFutureReservesResultCode.BEGIN_SPONSORING_FUTURE_RESERVES_RECURSIVE -> TsBeginSponsoringFutureReservesResultCode(
                        Code.OP_RECURSIVE,
                        c.getString(R.string.begin_sponsoring_future_reserves_recursive)
                    )
                    else -> TsBeginSponsoringFutureReservesResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsEndSponsoringFutureReservesResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: EndSponsoringFutureReservesResultCode?): TsEndSponsoringFutureReservesResultCode =
                when (code) {
                    EndSponsoringFutureReservesResultCode.END_SPONSORING_FUTURE_RESERVES_SUCCESS -> TsEndSponsoringFutureReservesResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    EndSponsoringFutureReservesResultCode.END_SPONSORING_FUTURE_RESERVES_NOT_SPONSORED -> TsEndSponsoringFutureReservesResultCode(
                        Code.OP_NOT_SPONSORED,
                        c.getString(R.string.end_sponsoring_future_reserves_not_sponsored)
                    )
                    else -> TsEndSponsoringFutureReservesResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsRevokeSponsorshipResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: RevokeSponsorshipResultCode?): TsRevokeSponsorshipResultCode =
                when (code) {
                    RevokeSponsorshipResultCode.REVOKE_SPONSORSHIP_SUCCESS -> TsRevokeSponsorshipResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    RevokeSponsorshipResultCode.REVOKE_SPONSORSHIP_DOES_NOT_EXIST -> TsRevokeSponsorshipResultCode(
                        Code.OP_DOES_NOT_EXIST,
                        c.getString(R.string.revoke_sponsorship_does_not_exist)
                    )
                    RevokeSponsorshipResultCode.REVOKE_SPONSORSHIP_NOT_SPONSOR -> TsRevokeSponsorshipResultCode(
                        Code.OP_NOT_SPONSOR,
                        c.getString(R.string.revoke_sponsorship_not_sponsor)
                    )
                    RevokeSponsorshipResultCode.REVOKE_SPONSORSHIP_LOW_RESERVE -> TsRevokeSponsorshipResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.revoke_sponsorship_low_reserve)
                    )
                    RevokeSponsorshipResultCode.REVOKE_SPONSORSHIP_ONLY_TRANSFERABLE -> TsRevokeSponsorshipResultCode(
                        Code.OP_ONLY_TRANSFERABLE,
                        c.getString(R.string.revoke_sponsorship_only_transferable)
                    )
                    RevokeSponsorshipResultCode.REVOKE_SPONSORSHIP_MALFORMED -> TsRevokeSponsorshipResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.revoke_sponsorship_malformed)
                    )
                    else -> TsRevokeSponsorshipResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsClawbackResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: ClawbackResultCode?): TsClawbackResultCode =
                when (code) {
                    ClawbackResultCode.CLAWBACK_SUCCESS -> TsClawbackResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    ClawbackResultCode.CLAWBACK_MALFORMED -> TsClawbackResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.clawback_malformed)
                    )
                    ClawbackResultCode.CLAWBACK_NOT_CLAWBACK_ENABLED -> TsClawbackResultCode(
                        Code.OP_NOT_CLAWBACK_ENABLED,
                        c.getString(R.string.clawback_not_clawback_enabled)
                    )
                    ClawbackResultCode.CLAWBACK_NO_TRUST -> TsClawbackResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.clawback_no_trust)
                    )
                    ClawbackResultCode.CLAWBACK_UNDERFUNDED -> TsClawbackResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.clawback_underfunded)
                    )
                    else -> TsClawbackResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsClawbackClaimableBalanceResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: ClawbackClaimableBalanceResultCode?): TsClawbackClaimableBalanceResultCode =
                when (code) {
                    ClawbackClaimableBalanceResultCode.CLAWBACK_CLAIMABLE_BALANCE_SUCCESS -> TsClawbackClaimableBalanceResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    ClawbackClaimableBalanceResultCode.CLAWBACK_CLAIMABLE_BALANCE_DOES_NOT_EXIST -> TsClawbackClaimableBalanceResultCode(
                        Code.OP_DOES_NOT_EXIST,
                        c.getString(R.string.clawback_claimable_balance_does_not_exist)
                    )
                    ClawbackClaimableBalanceResultCode.CLAWBACK_CLAIMABLE_BALANCE_NOT_ISSUER -> TsClawbackClaimableBalanceResultCode(
                        Code.OP_NOT_ISSUER,
                        c.getString(R.string.clawback_claimable_balance_not_issuer)
                    )
                    ClawbackClaimableBalanceResultCode.CLAWBACK_CLAIMABLE_BALANCE_NOT_CLAWBACK_ENABLED -> TsClawbackClaimableBalanceResultCode(
                        Code.OP_NOT_CLAWBACK_ENABLED,
                        c.getString(R.string.clawback_claimable_balance_not_clawback_enabled)
                    )
                    else -> TsClawbackClaimableBalanceResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsSetTrustLineFlagsResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: SetTrustLineFlagsResultCode?): TsSetTrustLineFlagsResultCode =
                when (code) {
                    SetTrustLineFlagsResultCode.SET_TRUST_LINE_FLAGS_SUCCESS -> TsSetTrustLineFlagsResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    SetTrustLineFlagsResultCode.SET_TRUST_LINE_FLAGS_MALFORMED -> TsSetTrustLineFlagsResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.set_trust_line_flags_malformed)
                    )
                    SetTrustLineFlagsResultCode.SET_TRUST_LINE_FLAGS_NO_TRUST_LINE -> TsSetTrustLineFlagsResultCode(
                        Code.OP_NO_TRUST_LINE, // NOTE from GO here OP_NO_TRUST
                        c.getString(R.string.set_trust_line_flags_no_trust_line)
                    )
                    SetTrustLineFlagsResultCode.SET_TRUST_LINE_FLAGS_CANT_REVOKE -> TsSetTrustLineFlagsResultCode(
                        Code.OP_CANT_REVOKE,
                        c.getString(R.string.set_trust_line_flags_cant_revoke)
                    )
                    SetTrustLineFlagsResultCode.SET_TRUST_LINE_FLAGS_INVALID_STATE -> TsSetTrustLineFlagsResultCode(
                        Code.OP_INVALID_STATE,
                        c.getString(R.string.set_trust_line_flags_invalid_state)
                    )
                    SetTrustLineFlagsResultCode.SET_TRUST_LINE_FLAGS_LOW_RESERVE -> TsSetTrustLineFlagsResultCode(
                        Code.OP_LOW_RESERVE,
                        c.getString(R.string.set_trust_line_flags_low_reserve)
                    )
                    else -> TsSetTrustLineFlagsResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsLiquidityPoolDepositResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: LiquidityPoolDepositResultCode?): TsLiquidityPoolDepositResultCode =
                when (code) {
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_SUCCESS -> TsLiquidityPoolDepositResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_MALFORMED -> TsLiquidityPoolDepositResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.liquidity_pool_deposit_malformed)
                    )
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_NO_TRUST -> TsLiquidityPoolDepositResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.liquidity_pool_deposit_no_trust)
                    )
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_NOT_AUTHORIZED -> TsLiquidityPoolDepositResultCode(
                        Code.OP_NOT_AUTHORIZED,
                        c.getString(R.string.liquidity_pool_deposit_not_authorized)
                    )
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_UNDERFUNDED -> TsLiquidityPoolDepositResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.liquidity_pool_deposit_underfunded)
                    )
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_LINE_FULL -> TsLiquidityPoolDepositResultCode(
                        Code.OP_LINE_FULL,
                        c.getString(R.string.liquidity_pool_deposit_line_full)
                    )
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_BAD_PRICE -> TsLiquidityPoolDepositResultCode(
                        Code.OP_BAD_PRICE,
                        c.getString(R.string.liquidity_pool_deposit_bad_price)
                    )
                    LiquidityPoolDepositResultCode.LIQUIDITY_POOL_DEPOSIT_POOL_FULL -> TsLiquidityPoolDepositResultCode(
                        Code.OP_POOL_FULL,
                        c.getString(R.string.liquidity_pool_deposit_pool_full)
                    )
                    else -> TsLiquidityPoolDepositResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }

    @Parcelize
    data class TsLiquidityPoolWithdrawResultCode(override var code: String, override var message: String) :
        OpResultCode(code, message), Parcelable {
        companion object {
            fun create(c: Context, code: LiquidityPoolWithdrawResultCode?): TsLiquidityPoolWithdrawResultCode =
                when (code) {
                    LiquidityPoolWithdrawResultCode.LIQUIDITY_POOL_WITHDRAW_SUCCESS -> TsLiquidityPoolWithdrawResultCode(
                        Code.OP_SUCCESS,
                        c.getString(R.string.op_success)
                    )
                    LiquidityPoolWithdrawResultCode.LIQUIDITY_POOL_WITHDRAW_MALFORMED -> TsLiquidityPoolWithdrawResultCode(
                        Code.OP_MALFORMED,
                        c.getString(R.string.liquidity_pool_withdraw_malformed)
                    )
                    LiquidityPoolWithdrawResultCode.LIQUIDITY_POOL_WITHDRAW_NO_TRUST -> TsLiquidityPoolWithdrawResultCode(
                        Code.OP_NO_TRUST,
                        c.getString(R.string.liquidity_pool_withdraw_no_trust)
                    )
                    LiquidityPoolWithdrawResultCode.LIQUIDITY_POOL_WITHDRAW_UNDERFUNDED -> TsLiquidityPoolWithdrawResultCode(
                        Code.OP_UNDERFUNDED,
                        c.getString(R.string.liquidity_pool_withdraw_underfunded)
                    )
                    LiquidityPoolWithdrawResultCode.LIQUIDITY_POOL_WITHDRAW_LINE_FULL -> TsLiquidityPoolWithdrawResultCode(
                        Code.OP_LINE_FULL,
                        c.getString(R.string.liquidity_pool_withdraw_line_full)
                    )
                    LiquidityPoolWithdrawResultCode.LIQUIDITY_POOL_WITHDRAW_UNDER_MINIMUM -> TsLiquidityPoolWithdrawResultCode(
                        Code.OP_UNDER_MINIMUM,
                        c.getString(R.string.liquidity_pool_withdraw_under_minimum)
                    )
                    else -> TsLiquidityPoolWithdrawResultCode(
                        Code.OP_UNDEFINED,
                        c.getString(R.string.op_undefined)
                    )
                }
        }
    }
}