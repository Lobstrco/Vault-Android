package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.*
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.entities.transaction.Claimant
import com.lobstr.stellar.vault.presentation.entities.transaction.Transaction
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.AccountMergeOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.AllowTrustOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.BumpSequenceOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.ChangeTrustOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.CreateAccountOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.InflationOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.ManageDataOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.PathPaymentStrictReceiveOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.PathPaymentStrictSendOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.PaymentOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.SetOptionsOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.SetTrustlineFlagsOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.SetTrustlineFlagsOperation.Companion.AUTHORIZED_FLAG
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.SetTrustlineFlagsOperation.Companion.AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.SetTrustlineFlagsOperation.Companion.TRUSTLINE_CLAWBACK_ENABLED_FLAG
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.claimable_balance.ClaimClaimableBalanceOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.claimable_balance.CreateClaimableBalanceOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.clawback.ClawbackClaimableBalanceOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.clawback.ClawbackOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.*
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.CreatePassiveSellOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.ManageBuyOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.ManageSellOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.BeginSponsoringFutureReservesOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.EndSponsoringFutureReservesOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.RevokeAccountSponsorshipOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.RevokeClaimableBalanceSponsorshipOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.RevokeDataSponsorshipOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.RevokeOfferSponsorshipOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.RevokeSignerSponsorshipOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.sponsoring.RevokeTrustlineSponsorshipOperation
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionType.Item.AUTH_CHALLENGE
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionType.Item.TRANSACTION
import com.lobstr.stellar.vault.presentation.util.StrKey
import org.stellar.sdk.*
import org.stellar.sdk.xdr.TrustLineFlags
import java.math.BigDecimal

class TransactionEntityMapper(private val network: Network) {

    companion object {
        const val HOME_DOMAIN_MANAGER_DATA_NAME_FLAG = "auth" // Equivalent of Sep10Challenge.HOME_DOMAIN_MANAGER_DATA_NAME_FLAG.
        const val WEB_AUTH_DOMAIN_MANAGER_DATA_NAME = "web_auth_domain" // Equivalent of Sep10Challenge.WEB_AUTH_DOMAIN_MANAGER_DATA_NAME.
    }

    fun transformTransactions(apiTransactionResult: ApiTransactionResult?): TransactionResult {
        if (apiTransactionResult == null) {
            return TransactionResult(
                null,
                null,
                0,
                mutableListOf()
            )
        }

        val transactions = mutableListOf<TransactionItem>()
        for (apiTransactionItem in apiTransactionResult.results!!) {
            if (apiTransactionItem != null) {
                transactions.add(transformTransactionItem(apiTransactionItem))
            }
        }

        return TransactionResult(
            apiTransactionResult.next,
            apiTransactionResult.previous,
            apiTransactionResult.count ?: 0,
            transactions
        )
    }

    fun transformTransactionItem(apiTransactionItem: ApiTransactionItem): TransactionItem {
        return TransactionItem(
            apiTransactionItem.cancelledAt,
            apiTransactionItem.addedAt,
            apiTransactionItem.signedAt,
            apiTransactionItem.hash!!,
            apiTransactionItem.getStatusDisplay,
            apiTransactionItem.status,
            apiTransactionItem.sequenceOutdatedAt,
            getTransaction(
                apiTransactionItem.xdr!!,
                apiTransactionItem.transactionType
            )
        )
    }

    fun transformTransactionXdr(xdr: String): TransactionItem {
        return TransactionItem(
            "",
            "",
            "",
            "",
            "",
            IMPORT_XDR,
            null,
            getTransaction(xdr)
        )
    }

    /**
     * Remove [HOME_DOMAIN_MANAGER_DATA_NAME_FLAG] from name to receive target domain.
     * @return domain without [HOME_DOMAIN_MANAGER_DATA_NAME_FLAG], if it is contained. Else - input.
     */
    private fun extractDomain(name: String?) = when {
        name.isNullOrEmpty() -> name
        name.contains(" $HOME_DOMAIN_MANAGER_DATA_NAME_FLAG") -> name.substring(0, name.lastIndexOf(" $HOME_DOMAIN_MANAGER_DATA_NAME_FLAG"))
        else -> name
    }

    /**
     * Check sep 10 challenge for determining transaction type.
     * domainName Retrieved from 'name' field value of [ManageDataOperation] without [HOME_DOMAIN_MANAGER_DATA_NAME_FLAG].
     * webAuthDomain The home domain that is expected to be included as the value of the [ManageDataOperation] with the 'web_auth_domain' key, if present.
     * @return transaction type: [TRANSACTION] or [AUTH_CHALLENGE].
     */
    fun getTransactionType(transaction: Transaction): String {
        val xdr: String = transaction.envelopXdr
        val sourceAccount: String = transaction.sourceAccount
        // Verify that the first operation in the transaction is a Manage Data operation for determine AUTH_CHALLENGE transaction type. Else - TRANSACTION type.
        val domainName: String? = if (transaction.operations.isNotEmpty() && transaction.operations[0] is ManageDataOperation)
            extractDomain((transaction.operations[0] as ManageDataOperation).name) else null
        val webAuthDomain: String? = if (transaction.operations.isNotEmpty() && transaction.operations[0] is ManageDataOperation)
            (transaction.operations.find { it is ManageDataOperation && it.name == WEB_AUTH_DOMAIN_MANAGER_DATA_NAME } as? ManageDataOperation)?.value
                ?.let { webAuthDomain -> String(webAuthDomain) } else null

        return if (domainName == null || getChallengeTransaction(xdr, sourceAccount, domainName, webAuthDomain) == null) {
            TRANSACTION
        } else {
            AUTH_CHALLENGE
        }
    }

    private fun getChallengeTransaction(
        challengeXdr: String,
        serverAccountId: String,
        domainName: String,
        webAuthDomain: String?
    ) =
        try {
            Sep10Challenge.readChallengeTransaction(
                challengeXdr,
                serverAccountId,
                network,
                domainName,
                webAuthDomain
            )
        } catch (exc: InvalidSep10ChallengeException) {
            null
        }

    private fun getTransaction(xdr: String, type: String? = null): Transaction {
        val transaction: AbstractTransaction = AbstractTransaction.fromEnvelopeXdr(xdr, network)
        val operations: MutableList<Operation> = mutableListOf()

        val targetTx = when (transaction) {
            is FeeBumpTransaction -> transaction.innerTransaction
            is org.stellar.sdk.Transaction -> transaction
            else -> throw Exception("Unknown transaction type.")
        }

        targetTx.operations.forEach {
            when (it) {
                is org.stellar.sdk.PaymentOperation -> operations.add(
                    mapPaymentOperation(it)
                )
                is org.stellar.sdk.CreateAccountOperation -> operations.add(
                    mapCreateAccountOperation(it)
                )
                is org.stellar.sdk.PathPaymentStrictSendOperation -> operations.add(
                    mapPathPaymentStrictSendOperation(it)
                )
                is org.stellar.sdk.PathPaymentStrictReceiveOperation -> operations.add(
                    mapPathPaymentStrictReceiveOperation(it)
                )
                is org.stellar.sdk.ManageSellOfferOperation -> operations.add(
                    mapManageSellOfferOperation(it)
                )
                is org.stellar.sdk.ManageBuyOfferOperation -> operations.add(
                    mapManageBuyOfferOperation(it)
                )
                is org.stellar.sdk.CreatePassiveSellOfferOperation -> operations.add(
                    mapCreatePassiveSellOfferOperation(
                        it
                    )
                )
                is org.stellar.sdk.SetOptionsOperation -> operations.add(mapSetOptionsOperation(it))
                is org.stellar.sdk.ChangeTrustOperation -> operations.add(mapChangeTrustOperation(it))
                is org.stellar.sdk.AllowTrustOperation -> operations.add(mapAllowTrustOperation(it)) // NOTE Remove in Future (use instead SetTrustlineFlagsOperation).
                is org.stellar.sdk.SetTrustlineFlagsOperation -> operations.add(mapSetTrustlineFlagsOperation(it))
                is org.stellar.sdk.AccountMergeOperation -> operations.add(
                    mapAccountMergeOperation(
                        it
                    )
                )
                is org.stellar.sdk.InflationOperation -> operations.add(mapInflationOperation(it))
                is org.stellar.sdk.ManageDataOperation -> operations.add(mapManageDataOperation(it))
                is org.stellar.sdk.BumpSequenceOperation -> operations.add(
                    mapBumpSequenceOperation(
                        it
                    )
                )
                is org.stellar.sdk.BeginSponsoringFutureReservesOperation -> operations.add(mapBeginSponsoringFutureReservesOperation(it))

                is org.stellar.sdk.EndSponsoringFutureReservesOperation -> operations.add(mapEndSponsoringFutureReservesOperation(it))
                is org.stellar.sdk.RevokeAccountSponsorshipOperation -> operations.add(mapRevokeAccountSponsorshipOperation(it))
                is org.stellar.sdk.RevokeClaimableBalanceSponsorshipOperation -> operations.add(mapRevokeClaimableBalanceSponsorshipOperation(it))
                is org.stellar.sdk.RevokeDataSponsorshipOperation -> operations.add(mapRevokeDataSponsorshipOperation(it))
                is org.stellar.sdk.RevokeOfferSponsorshipOperation -> operations.add(mapRevokeOfferSponsorshipOperation(it))
                is org.stellar.sdk.RevokeSignerSponsorshipOperation -> operations.add(mapRevokeSignerSponsorshipOperation(it))
                is org.stellar.sdk.RevokeTrustlineSponsorshipOperation -> operations.add(mapRevokeTrustlineSponsorshipOperation(it))
                is org.stellar.sdk.CreateClaimableBalanceOperation -> operations.add(mapCreateClaimableBalanceOperation(it))
                is org.stellar.sdk.ClaimClaimableBalanceOperation -> operations.add(mapClaimClaimableBalanceOperation(it))
                is org.stellar.sdk.ClawbackClaimableBalanceOperation -> operations.add(mapClawbackClaimableBalanceOperation(it))
                is org.stellar.sdk.ClawbackOperation -> operations.add(mapClawbackOperation(it))
            }
        }

        return Transaction(
            targetTx.toEnvelopeXdrBase64(),
            targetTx.sourceAccount,
            mapMemo(targetTx.memo),
            operations,
            targetTx.sequenceNumber
        ).apply {
            transactionType = if (type.isNullOrEmpty()) getTransactionType(this) else type
        }
    }

    private fun mapPaymentOperation(
        operation: org.stellar.sdk.PaymentOperation
    ): PaymentOperation {
        return PaymentOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.destination,
            mapAsset(operation.asset),
            operation.amount
        )
    }

    private fun mapMemo(memo: Memo): String = when(memo) {
        is MemoHash -> memo.hexValue
        is MemoReturnHash -> memo.hexValue
        else -> memo.toString()
    }

    private fun mapCreateAccountOperation(operation: org.stellar.sdk.CreateAccountOperation): CreateAccountOperation {
        return CreateAccountOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.destination,
            operation.startingBalance
        )
    }

    private fun mapPathPaymentStrictSendOperation(operation: org.stellar.sdk.PathPaymentStrictSendOperation): PathPaymentStrictSendOperation {
        return PathPaymentStrictSendOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            mapAsset(operation.sendAsset),
            operation.sendAmount,
            operation.destination,
            mapAsset(operation.destAsset),
            operation.destMin,
            if (operation.path.isNotEmpty()) {
                val path: MutableList<Asset> = mutableListOf()
                operation.path.forEach {
                    path.add(mapAsset(it))
                }
                path
            } else {
                null
            }
        )
    }

    private fun mapPathPaymentStrictReceiveOperation(operation: org.stellar.sdk.PathPaymentStrictReceiveOperation): PathPaymentStrictReceiveOperation {
        return PathPaymentStrictReceiveOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            mapAsset(operation.sendAsset),
            operation.sendMax,
            operation.destination,
            mapAsset(operation.destAsset),
            operation.destAmount,
            if (operation.path.isNotEmpty()) {
                val path: MutableList<Asset> = mutableListOf()
                operation.path.forEach {
                    path.add(mapAsset(it))
                }
                path
            } else {
                null
            }
        )
    }

    private fun mapManageSellOfferOperation(operation: org.stellar.sdk.ManageSellOfferOperation): ManageSellOfferOperation {
        // Determine sell offer operation type: SellOfferOperation or CancelSellOfferOperation.
        return if (operation.amount.isEmpty() || operation.amount == "0") {
            CancelSellOfferOperation(
                (operation as org.stellar.sdk.Operation).sourceAccount,
                mapAsset(operation.selling),
                mapAsset(operation.buying),
                BigDecimal(operation.price).stripTrailingZeros().toPlainString(),
                operation.offerId
            )
        } else {
            SellOfferOperation(
                (operation as org.stellar.sdk.Operation).sourceAccount,
                mapAsset(operation.selling),
                mapAsset(operation.buying),
                operation.amount,
                BigDecimal(operation.price).stripTrailingZeros().toPlainString()
            )
        }
    }

    private fun mapManageBuyOfferOperation(operation: org.stellar.sdk.ManageBuyOfferOperation): ManageBuyOfferOperation {
        return ManageBuyOfferOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            mapAsset(operation.selling),
            mapAsset(operation.buying),
            operation.amount,
            BigDecimal(operation.price).stripTrailingZeros().toPlainString()
        )
    }

    private fun mapCreatePassiveSellOfferOperation(operation: org.stellar.sdk.CreatePassiveSellOfferOperation): CreatePassiveSellOfferOperation {
        return CreatePassiveSellOfferOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            mapAsset(operation.selling),
            mapAsset(operation.buying),
            operation.amount,
            BigDecimal(operation.price).stripTrailingZeros().toPlainString()
        )
    }

    private fun mapSetOptionsOperation(operation: org.stellar.sdk.SetOptionsOperation): SetOptionsOperation {
        return SetOptionsOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.inflationDestination,
            operation.clearFlags,
            operation.setFlags,
            operation.masterKeyWeight,
            operation.lowThreshold,
            operation.mediumThreshold,
            operation.highThreshold,
            operation.homeDomain,
            operation.signerWeight,
            try {
                StrKey.encodeStellarAccountId(operation.signer.ed25519.uint256)
            } catch (e: Exception) {
                null
            }
        )
    }

    private fun mapChangeTrustOperation(operation: org.stellar.sdk.ChangeTrustOperation): ChangeTrustOperation {
        return ChangeTrustOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            mapAsset(operation.asset),
            operation.limit
        )
    }

    private fun mapAllowTrustOperation(operation: org.stellar.sdk.AllowTrustOperation): AllowTrustOperation {
        return AllowTrustOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.trustor,
            operation.assetCode,
            operation.authorize
        )
    }

    private fun mapSetTrustlineFlagsOperation(operation: org.stellar.sdk.SetTrustlineFlagsOperation): SetTrustlineFlagsOperation {
        return SetTrustlineFlagsOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.trustor,
            mapAsset(operation.asset),
            if (!operation.clearFlags.isNullOrEmpty()) operation.clearFlags.map { mapTrustlineFlag(it) } else null,
            if (!operation.setFlags.isNullOrEmpty()) operation.setFlags.map { mapTrustlineFlag(it) } else null
        )
    }

    private fun mapTrustlineFlag(flag: TrustLineFlags): Int {
        return when (flag) {
            TrustLineFlags.AUTHORIZED_FLAG -> AUTHORIZED_FLAG
            TrustLineFlags.AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG -> AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG
            TrustLineFlags.TRUSTLINE_CLAWBACK_ENABLED_FLAG -> TRUSTLINE_CLAWBACK_ENABLED_FLAG
            else -> Constant.Util.UNDEFINED_VALUE
        }
    }

    private fun mapAccountMergeOperation(operation: org.stellar.sdk.AccountMergeOperation): AccountMergeOperation {
        return AccountMergeOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.destination
        )
    }

    private fun mapInflationOperation(operation: org.stellar.sdk.InflationOperation): InflationOperation {
        return InflationOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount
        )
    }

    private fun mapManageDataOperation(operation: org.stellar.sdk.ManageDataOperation): ManageDataOperation {
        return ManageDataOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.name,
            operation.value
        )
    }

    private fun mapBumpSequenceOperation(operation: org.stellar.sdk.BumpSequenceOperation): BumpSequenceOperation {
        return BumpSequenceOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.bumpTo
        )
    }

    private fun mapBeginSponsoringFutureReservesOperation(operation: org.stellar.sdk.BeginSponsoringFutureReservesOperation): BeginSponsoringFutureReservesOperation {
        return BeginSponsoringFutureReservesOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.sponsoredId
        )
    }

    private fun mapEndSponsoringFutureReservesOperation(operation: org.stellar.sdk.EndSponsoringFutureReservesOperation): EndSponsoringFutureReservesOperation {
        return EndSponsoringFutureReservesOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount
        )
    }

    private fun mapRevokeAccountSponsorshipOperation(operation: org.stellar.sdk.RevokeAccountSponsorshipOperation): RevokeAccountSponsorshipOperation {
        return RevokeAccountSponsorshipOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.accountId
        )
    }

    private fun mapRevokeClaimableBalanceSponsorshipOperation(operation: org.stellar.sdk.RevokeClaimableBalanceSponsorshipOperation): RevokeClaimableBalanceSponsorshipOperation {
        return RevokeClaimableBalanceSponsorshipOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.balanceId
        )
    }

    private fun mapRevokeDataSponsorshipOperation(operation: org.stellar.sdk.RevokeDataSponsorshipOperation): RevokeDataSponsorshipOperation {
        return RevokeDataSponsorshipOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.accountId,
            operation.dataName
        )
    }

    private fun mapRevokeOfferSponsorshipOperation(operation: org.stellar.sdk.RevokeOfferSponsorshipOperation): RevokeOfferSponsorshipOperation {
        return RevokeOfferSponsorshipOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.seller,
            operation.offerId
        )
    }

    private fun mapRevokeSignerSponsorshipOperation(operation: org.stellar.sdk.RevokeSignerSponsorshipOperation): RevokeSignerSponsorshipOperation {
        return RevokeSignerSponsorshipOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.accountId,
            try {
                KeyPair.fromXdrSignerKey(operation.signer).accountId
            } catch (e: Exception) {
                null
            }
        )
    }

    private fun mapRevokeTrustlineSponsorshipOperation(operation: org.stellar.sdk.RevokeTrustlineSponsorshipOperation): RevokeTrustlineSponsorshipOperation {
        return RevokeTrustlineSponsorshipOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.accountId,
            mapAsset(operation.asset)
        )
    }

    private fun mapCreateClaimableBalanceOperation(operation: org.stellar.sdk.CreateClaimableBalanceOperation): CreateClaimableBalanceOperation {
        return CreateClaimableBalanceOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.amount,
            mapAsset(operation.asset),
            mutableListOf<Claimant>().apply {
                operation.claimants.forEach {
                    add(mapClaimant(it))
                }
            }
        )
    }

    private fun mapClaimClaimableBalanceOperation(operation: org.stellar.sdk.ClaimClaimableBalanceOperation): ClaimClaimableBalanceOperation {
        return ClaimClaimableBalanceOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.balanceId
        )
    }

    private fun mapClawbackClaimableBalanceOperation(operation: org.stellar.sdk.ClawbackClaimableBalanceOperation): ClawbackClaimableBalanceOperation {
        return ClawbackClaimableBalanceOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.balanceId
        )
    }

    private fun mapClawbackOperation(operation: org.stellar.sdk.ClawbackOperation): ClawbackOperation {
        return ClawbackOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.from,
            mapAsset(operation.asset),
            operation.amount
        )
    }

    private fun mapAsset(asset: org.stellar.sdk.Asset?): Asset {
        return when (asset) {
            is AssetTypeCreditAlphaNum -> Asset(asset.code, asset.type, asset.issuer)
            is AssetTypeNative -> Asset("XLM", "native", null)
            else -> Asset("XLM", "native", null)
        }
    }

    private fun mapClaimant(claimant: org.stellar.sdk.Claimant): Claimant {
        return Claimant(claimant.destination)
    }
}