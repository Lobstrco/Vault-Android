package com.lobstr.stellar.tsmapper.data.transaction

import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.Firebase
import com.lobstr.stellar.tsmapper.data.asset.AssetMapper
import com.lobstr.stellar.tsmapper.data.claim.ClaimantMapper
import com.lobstr.stellar.tsmapper.data.soroban.host_function.InvokeHostFunctionMapper
import com.lobstr.stellar.tsmapper.data.soroban.data.SorobanDataMapper
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.Transaction
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.TsMemo
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.TsMemo.*
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.TsTimeBounds
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.AssetType
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.AccountMergeOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.AllowTrustOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.BumpSequenceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.ChangeTrustOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.CreateAccountOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.InflationOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.ManageDataOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.PathPaymentStrictReceiveOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.PathPaymentStrictSendOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.PaymentOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.SetOptionsOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.SetTrustlineFlagsOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance.ClaimClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.claimable_balance.CreateClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.clawback.ClawbackClaimableBalanceOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.clawback.ClawbackOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool.LiquidityPoolDepositOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.liquidity_pool.LiquidityPoolWithdrawOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.*
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.CreatePassiveSellOfferOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.ManageBuyOfferOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.offer.ManageSellOfferOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.ExtendFootprintTTLOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.InvokeHostFunctionOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.soroban.RestoreFootprintOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.BeginSponsoringFutureReservesOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.EndSponsoringFutureReservesOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.RevokeAccountSponsorshipOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.RevokeClaimableBalanceSponsorshipOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.RevokeDataSponsorshipOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.RevokeOfferSponsorshipOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.RevokeSignerSponsorshipOperation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.sponsoring.RevokeTrustlineSponsorshipOperation
import com.lobstr.stellar.tsmapper.presentation.util.Constant
import com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.AUTH_CHALLENGE
import com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.TRANSACTION
import org.stellar.sdk.*
import org.stellar.sdk.exception.InvalidSep10ChallengeException
import org.stellar.sdk.operations.AllowTrustOperation.TrustLineEntryFlag
import org.stellar.sdk.xdr.TrustLineFlags
import java.math.BigDecimal

class TsMapper(
    private val network: Network = Network.PUBLIC,
    private val claimantMapper: ClaimantMapper = ClaimantMapper(),
    private val assetMapper: AssetMapper = AssetMapper(),
    private val invokeHostFunctionMapper: InvokeHostFunctionMapper = InvokeHostFunctionMapper(assetMapper),
    private val sorobanDataMapper: SorobanDataMapper = SorobanDataMapper()
) {

    companion object {
        const val HOME_DOMAIN_MANAGER_DATA_NAME_FLAG = "auth" // Equivalent of Sep10Challenge.HOME_DOMAIN_MANAGER_DATA_NAME_FLAG.
        const val WEB_AUTH_DOMAIN_MANAGER_DATA_NAME = "web_auth_domain" // Equivalent of Sep10Challenge.WEB_AUTH_DOMAIN_MANAGER_DATA_NAME.
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

    /**
     * Retrieve Inner Transaction.
     * @param xdr - XDR.
     * @param type - transaction type. See [Constant.TransactionType]
     * @return [Transaction].
     */
    fun getTransaction(xdr: String, type: String? = null): Transaction {
        val transaction: AbstractTransaction = AbstractTransaction.fromEnvelopeXdr(xdr, network)
        val operations: MutableList<Operation> = mutableListOf()

        val targetTx = when (transaction) {
            is FeeBumpTransaction -> transaction.innerTransaction
            is org.stellar.sdk.Transaction -> transaction
            else -> throw Exception("Unknown transaction type.")
        }

        targetTx.operations.forEach {
            try {
                when (it) {
                    is org.stellar.sdk.operations.PaymentOperation -> operations.add(
                        mapPaymentOperation(it)
                    )

                    is org.stellar.sdk.operations.CreateAccountOperation -> operations.add(
                        mapCreateAccountOperation(it)
                    )

                    is org.stellar.sdk.operations.PathPaymentStrictSendOperation -> operations.add(
                        mapPathPaymentStrictSendOperation(it)
                    )

                    is org.stellar.sdk.operations.PathPaymentStrictReceiveOperation -> operations.add(
                        mapPathPaymentStrictReceiveOperation(it)
                    )

                    is org.stellar.sdk.operations.ManageSellOfferOperation -> operations.add(
                        mapManageSellOfferOperation(it)
                    )

                    is org.stellar.sdk.operations.ManageBuyOfferOperation -> operations.add(
                        mapManageBuyOfferOperation(it)
                    )

                    is org.stellar.sdk.operations.CreatePassiveSellOfferOperation -> operations.add(
                        mapCreatePassiveSellOfferOperation(
                            it
                        )
                    )

                    is org.stellar.sdk.operations.SetOptionsOperation -> operations.add(
                        mapSetOptionsOperation(
                            it
                        )
                    )

                    is org.stellar.sdk.operations.ChangeTrustOperation -> operations.add(
                        mapChangeTrustOperation(it)
                    )

                    is org.stellar.sdk.operations.AllowTrustOperation -> operations.add(
                        mapAllowTrustOperation(
                            it
                        )
                    ) // NOTE Remove in Future (use instead SetTrustlineFlagsOperation).
                    is org.stellar.sdk.operations.SetTrustlineFlagsOperation -> operations.add(
                        mapSetTrustlineFlagsOperation(it)
                    )

                    is org.stellar.sdk.operations.AccountMergeOperation -> operations.add(
                        mapAccountMergeOperation(
                            it
                        )
                    )

                    is org.stellar.sdk.operations.InflationOperation -> operations.add(mapInflationOperation(it))
                    is org.stellar.sdk.operations.ManageDataOperation -> operations.add(
                        mapManageDataOperation(
                            it
                        )
                    )

                    is org.stellar.sdk.operations.BumpSequenceOperation -> operations.add(
                        mapBumpSequenceOperation(
                            it
                        )
                    )

                    is org.stellar.sdk.operations.BeginSponsoringFutureReservesOperation -> operations.add(
                        mapBeginSponsoringFutureReservesOperation(it)
                    )

                    is org.stellar.sdk.operations.EndSponsoringFutureReservesOperation -> operations.add(
                        mapEndSponsoringFutureReservesOperation(it)
                    )

                    is org.stellar.sdk.operations.RevokeAccountSponsorshipOperation -> operations.add(
                        mapRevokeAccountSponsorshipOperation(it)
                    )

                    is org.stellar.sdk.operations.RevokeClaimableBalanceSponsorshipOperation -> operations.add(
                        mapRevokeClaimableBalanceSponsorshipOperation(it)
                    )

                    is org.stellar.sdk.operations.RevokeDataSponsorshipOperation -> operations.add(
                        mapRevokeDataSponsorshipOperation(it)
                    )

                    is org.stellar.sdk.operations.RevokeOfferSponsorshipOperation -> operations.add(
                        mapRevokeOfferSponsorshipOperation(it)
                    )

                    is org.stellar.sdk.operations.RevokeSignerSponsorshipOperation -> operations.add(
                        mapRevokeSignerSponsorshipOperation(it)
                    )

                    is org.stellar.sdk.operations.RevokeTrustlineSponsorshipOperation -> operations.add(
                        mapRevokeTrustlineSponsorshipOperation(it)
                    )

                    is org.stellar.sdk.operations.CreateClaimableBalanceOperation -> operations.add(
                        mapCreateClaimableBalanceOperation(it)
                    )

                    is org.stellar.sdk.operations.ClaimClaimableBalanceOperation -> operations.add(
                        mapClaimClaimableBalanceOperation(it)
                    )

                    is org.stellar.sdk.operations.ClawbackClaimableBalanceOperation -> operations.add(
                        mapClawbackClaimableBalanceOperation(it)
                    )

                    is org.stellar.sdk.operations.ClawbackOperation -> operations.add(mapClawbackOperation(it))
                    is org.stellar.sdk.operations.LiquidityPoolDepositOperation -> operations.add(
                        mapLiquidityPoolDepositOperation(it)
                    )

                    is org.stellar.sdk.operations.LiquidityPoolWithdrawOperation -> operations.add(
                        mapLiquidityPoolWithdrawOperation(it)
                    )
                    // Soroban.
                    is org.stellar.sdk.operations.ExtendFootprintTTLOperation -> operations.add(
                        mapExtendFootprintTTLOperation(it)
                    )

                    is org.stellar.sdk.operations.RestoreFootprintOperation -> operations.add(
                        mapRestoreFootprintOperation(it)
                    )

                    is org.stellar.sdk.operations.InvokeHostFunctionOperation -> operations.add(
                        mapInvokeHostFunctionOperation(it)
                    )
                }
            } catch (exc: Exception) {
                // Skip Unmappable operations.
                Firebase.crashlytics.recordException(exc)
            }
        }

        return Transaction(
            targetTx.hashHex(),
            targetTx.toEnvelopeXdrBase64(),
            targetTx.fee,
            targetTx.sourceAccount,
            mapMemo(targetTx.memo),
            TsTimeBounds(
                targetTx.preconditions?.timeBounds?.minTime?.toLong() ?: 0,
                targetTx.preconditions?.timeBounds?.maxTime?.toLong() ?: 0
            ),
            operations,
            targetTx.sequenceNumber,
            sorobanData = if (targetTx.isSorobanTransaction) {
                try {
                    sorobanDataMapper.mapSorobanData(
                        targetTx.sorobanData
                    )
                } catch (exc: Exception) {
                    Firebase.crashlytics.recordException(exc)
                    null
                }
            } else null
        ).apply {
            transactionType = if (type.isNullOrEmpty()) getTransactionType(this) else type
        }
    }

    private fun mapPaymentOperation(
        operation: org.stellar.sdk.operations.PaymentOperation
    ): PaymentOperation {
        return PaymentOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.destination,
            assetMapper.mapAsset(operation.asset),
            operation.amount.stripTrailingZeros().toPlainString()
        )
    }

    private fun mapMemo(memo: Memo): TsMemo = when (memo) {
        is MemoHash -> MEMO_HASH(memo.hexValue)
        is MemoReturnHash -> MEMO_RETURN(memo.hexValue)
        is MemoId -> MEMO_ID(memo.toString())
        is MemoText -> MEMO_TEXT(memo.toString())
        is MemoNone -> MEMO_NONE(memo.toString())
        else -> MEMO_NONE(memo.toString())
    }

    private fun mapCreateAccountOperation(operation: org.stellar.sdk.operations.CreateAccountOperation): CreateAccountOperation {
        return CreateAccountOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.destination,
            Asset.CanonicalAsset(AssetType.ASSET_TYPE_NATIVE,"XLM", null), // Add XLM for better data representation.
            operation.startingBalance.stripTrailingZeros().toPlainString()
        )
    }

    private fun mapPathPaymentStrictSendOperation(operation: org.stellar.sdk.operations.PathPaymentStrictSendOperation): PathPaymentStrictSendOperation {
        return PathPaymentStrictSendOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            assetMapper.mapAsset(operation.sendAsset),
            operation.sendAmount.stripTrailingZeros().toPlainString(),
            operation.destination,
            assetMapper.mapAsset(operation.destAsset),
            operation.destMin.stripTrailingZeros().toPlainString(),
            if (operation.path.isNotEmpty()) {
                val path: MutableList<Asset.CanonicalAsset> = mutableListOf()
                operation.path.forEach {
                    path.add(assetMapper.mapAsset(it))
                }
                path
            } else {
                null
            }
        )
    }

    private fun mapPathPaymentStrictReceiveOperation(operation: org.stellar.sdk.operations.PathPaymentStrictReceiveOperation): PathPaymentStrictReceiveOperation {
        return PathPaymentStrictReceiveOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            assetMapper.mapAsset(operation.sendAsset),
            operation.sendMax.stripTrailingZeros().toPlainString(),
            operation.destination,
            assetMapper.mapAsset(operation.destAsset),
            operation.destAmount.stripTrailingZeros().toPlainString(),
            if (operation.path.isNotEmpty()) {
                val path: MutableList<Asset.CanonicalAsset> = mutableListOf()
                operation.path.forEach {
                    path.add(assetMapper.mapAsset(it))
                }
                path
            } else {
                null
            }
        )
    }

    private fun mapManageSellOfferOperation(operation: org.stellar.sdk.operations.ManageSellOfferOperation): ManageSellOfferOperation {
        // Determine sell offer operation type: SellOfferOperation or CancelSellOfferOperation.
        return if (operation.amount.compareTo(BigDecimal.ZERO) == 0) {
            CancelSellOfferOperation(
                (operation as org.stellar.sdk.operations.Operation).sourceAccount,
                assetMapper.mapAsset(operation.selling),
                assetMapper.mapAsset(operation.buying),
                operation.amount.stripTrailingZeros().toPlainString(),
                BigDecimal(operation.price.toString()).stripTrailingZeros().toPlainString(),
                operation.offerId
            )
        } else {
            SellOfferOperation(
                (operation as org.stellar.sdk.operations.Operation).sourceAccount,
                assetMapper.mapAsset(operation.selling),
                assetMapper.mapAsset(operation.buying),
                operation.amount.stripTrailingZeros().toPlainString(),
                BigDecimal(operation.price.toString()).stripTrailingZeros().toPlainString(),
                operation.offerId
            )
        }
    }

    private fun mapManageBuyOfferOperation(operation: org.stellar.sdk.operations.ManageBuyOfferOperation): ManageBuyOfferOperation {
        // Determine buy offer operation type: BuyOfferOperation or CancelBuyOfferOperation.
        return if (operation.amount.compareTo(BigDecimal.ZERO) == 0) {
            CancelBuyOfferOperation(
                (operation as org.stellar.sdk.operations.Operation).sourceAccount,
                assetMapper.mapAsset(operation.selling),
                assetMapper.mapAsset(operation.buying),
                operation.amount.stripTrailingZeros().toPlainString(),
                BigDecimal(operation.price.toString()).stripTrailingZeros().toPlainString(),
                operation.offerId
            )
        } else {
            BuyOfferOperation(
                (operation as org.stellar.sdk.operations.Operation).sourceAccount,
                assetMapper.mapAsset(operation.selling),
                assetMapper.mapAsset(operation.buying),
                operation.amount.stripTrailingZeros().toPlainString(),
                BigDecimal(operation.price.toString()).stripTrailingZeros().toPlainString(),
                operation.offerId
            )
        }
    }

    private fun mapCreatePassiveSellOfferOperation(operation: org.stellar.sdk.operations.CreatePassiveSellOfferOperation): CreatePassiveSellOfferOperation {
        return CreatePassiveSellOfferOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            assetMapper.mapAsset(operation.selling),
            assetMapper.mapAsset(operation.buying),
            operation.amount.stripTrailingZeros().toPlainString(),
            BigDecimal(operation.price.toString()).stripTrailingZeros().toPlainString()
        )
    }

    private fun mapSetOptionsOperation(operation: org.stellar.sdk.operations.SetOptionsOperation): SetOptionsOperation {
        return SetOptionsOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
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
                StrKey.encodeEd25519PublicKey(operation.signer?.ed25519?.uint256)
            } catch (e: Exception) {
                null
            }
        )
    }

    private fun mapChangeTrustOperation(operation: org.stellar.sdk.operations.ChangeTrustOperation): ChangeTrustOperation {
        return ChangeTrustOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            assetMapper.mapChangeTrustAsset(operation.asset),
            operation.limit.stripTrailingZeros().toPlainString()
        )
    }

    private fun mapAllowTrustOperation(operation: org.stellar.sdk.operations.AllowTrustOperation): AllowTrustOperation {
        return AllowTrustOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.trustor,
            operation.assetCode,
            mapTrustlineEntryFlag(operation.authorize)
        )
    }

    private fun mapSetTrustlineFlagsOperation(operation: org.stellar.sdk.operations.SetTrustlineFlagsOperation): SetTrustlineFlagsOperation {
        return SetTrustlineFlagsOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.trustor,
            assetMapper.mapAsset(operation.asset),
            if (!operation.clearFlags.isEmpty()) operation.clearFlags.map { mapTrustlineFlag(it) } else null,
            if (!operation.setFlags.isEmpty()) operation.setFlags.map { mapTrustlineFlag(it) } else null
        )
    }

    private fun mapTrustlineFlag(flag: TrustLineFlags): Int {
        return when (flag) {
            TrustLineFlags.AUTHORIZED_FLAG -> SetTrustlineFlagsOperation.AUTHORIZED_FLAG
            TrustLineFlags.AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG -> SetTrustlineFlagsOperation.AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG
            TrustLineFlags.TRUSTLINE_CLAWBACK_ENABLED_FLAG -> SetTrustlineFlagsOperation.TRUSTLINE_CLAWBACK_ENABLED_FLAG
            else -> Constant.Util.UNDEFINED_VALUE
        }
    }

    private fun mapTrustlineEntryFlag(flag: TrustLineEntryFlag): Int {
        return when (flag) {
            TrustLineEntryFlag.UNAUTHORIZED_FLAG -> AllowTrustOperation.UNAUTHORIZED_FLAG
            TrustLineEntryFlag.AUTHORIZED_FLAG -> AllowTrustOperation.AUTHORIZED_FLAG
            TrustLineEntryFlag.AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG -> AllowTrustOperation.AUTHORIZED_TO_MAINTAIN_LIABILITIES_FLAG
            else -> Constant.Util.UNDEFINED_VALUE
        }
    }

    private fun mapAccountMergeOperation(operation: org.stellar.sdk.operations.AccountMergeOperation): AccountMergeOperation {
        return AccountMergeOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.destination
        )
    }

    private fun mapInflationOperation(operation: org.stellar.sdk.operations.InflationOperation): InflationOperation {
        return InflationOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount
        )
    }

    private fun mapManageDataOperation(operation: org.stellar.sdk.operations.ManageDataOperation): ManageDataOperation {
        return ManageDataOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.name,
            operation.value
        )
    }

    private fun mapBumpSequenceOperation(operation: org.stellar.sdk.operations.BumpSequenceOperation): BumpSequenceOperation {
        return BumpSequenceOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.bumpTo
        )
    }

    private fun mapBeginSponsoringFutureReservesOperation(operation: org.stellar.sdk.operations.BeginSponsoringFutureReservesOperation): BeginSponsoringFutureReservesOperation {
        return BeginSponsoringFutureReservesOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.sponsoredId
        )
    }

    private fun mapEndSponsoringFutureReservesOperation(operation: org.stellar.sdk.operations.EndSponsoringFutureReservesOperation): EndSponsoringFutureReservesOperation {
        return EndSponsoringFutureReservesOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount
        )
    }

    private fun mapRevokeAccountSponsorshipOperation(operation: org.stellar.sdk.operations.RevokeAccountSponsorshipOperation): RevokeAccountSponsorshipOperation {
        return RevokeAccountSponsorshipOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.accountId
        )
    }

    private fun mapRevokeClaimableBalanceSponsorshipOperation(operation: org.stellar.sdk.operations.RevokeClaimableBalanceSponsorshipOperation): RevokeClaimableBalanceSponsorshipOperation {
        return RevokeClaimableBalanceSponsorshipOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.balanceId
        )
    }

    private fun mapRevokeDataSponsorshipOperation(operation: org.stellar.sdk.operations.RevokeDataSponsorshipOperation): RevokeDataSponsorshipOperation {
        return RevokeDataSponsorshipOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.accountId,
            operation.dataName
        )
    }

    private fun mapRevokeOfferSponsorshipOperation(operation: org.stellar.sdk.operations.RevokeOfferSponsorshipOperation): RevokeOfferSponsorshipOperation {
        return RevokeOfferSponsorshipOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.seller,
            operation.offerId
        )
    }

    private fun mapRevokeSignerSponsorshipOperation(operation: org.stellar.sdk.operations.RevokeSignerSponsorshipOperation): RevokeSignerSponsorshipOperation {
        return RevokeSignerSponsorshipOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.accountId,
            try {
                KeyPair.fromXdrSignerKey(operation.signer).accountId
            } catch (e: Exception) {
                null
            }
        )
    }

    private fun mapRevokeTrustlineSponsorshipOperation(operation: org.stellar.sdk.operations.RevokeTrustlineSponsorshipOperation): RevokeTrustlineSponsorshipOperation {
        return RevokeTrustlineSponsorshipOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.accountId,
            assetMapper.mapTrustLineAsset(operation.asset)
        )
    }

    private fun mapCreateClaimableBalanceOperation(operation: org.stellar.sdk.operations.CreateClaimableBalanceOperation): CreateClaimableBalanceOperation {
        return CreateClaimableBalanceOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.amount.stripTrailingZeros().toPlainString(),
            assetMapper.mapAsset(operation.asset),
            claimantMapper.mapClaimants(operation.claimants)
        )
    }

    private fun mapClaimClaimableBalanceOperation(operation: org.stellar.sdk.operations.ClaimClaimableBalanceOperation): ClaimClaimableBalanceOperation {
        return ClaimClaimableBalanceOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.balanceId
        )
    }

    private fun mapClawbackClaimableBalanceOperation(operation: org.stellar.sdk.operations.ClawbackClaimableBalanceOperation): ClawbackClaimableBalanceOperation {
        return ClawbackClaimableBalanceOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.balanceId
        )
    }

    private fun mapClawbackOperation(operation: org.stellar.sdk.operations.ClawbackOperation): ClawbackOperation {
        return ClawbackOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.from,
            assetMapper.mapAsset(operation.asset),
            operation.amount.stripTrailingZeros().toPlainString()
        )
    }

    private fun mapLiquidityPoolDepositOperation(operation: org.stellar.sdk.operations.LiquidityPoolDepositOperation): LiquidityPoolDepositOperation {
        return LiquidityPoolDepositOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.liquidityPoolId,
            operation.maxAmountA.stripTrailingZeros().toPlainString(),
            operation.maxAmountB.stripTrailingZeros().toPlainString(),
            try { BigDecimal(operation.minPrice.toString()).stripTrailingZeros().toPlainString() } catch (exc: Exception) { null },
            try { BigDecimal(operation.maxPrice.toString()).stripTrailingZeros().toPlainString() } catch (exc: Exception) { null },
        )
    }

    private fun mapLiquidityPoolWithdrawOperation(operation: org.stellar.sdk.operations.LiquidityPoolWithdrawOperation): LiquidityPoolWithdrawOperation {
        return LiquidityPoolWithdrawOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.liquidityPoolId,
            operation.amount.stripTrailingZeros().toPlainString(),
            operation.minAmountA.stripTrailingZeros().toPlainString(),
            operation.minAmountB.stripTrailingZeros().toPlainString(),
        )
    }

    private fun mapExtendFootprintTTLOperation(operation: org.stellar.sdk.operations.ExtendFootprintTTLOperation): ExtendFootprintTTLOperation {
        return ExtendFootprintTTLOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount,
            operation.extendTo
        )
    }

    private fun mapRestoreFootprintOperation(operation: org.stellar.sdk.operations.RestoreFootprintOperation): RestoreFootprintOperation {
        return RestoreFootprintOperation(
            (operation as org.stellar.sdk.operations.Operation).sourceAccount
        )
    }

    private fun mapInvokeHostFunctionOperation(operation: org.stellar.sdk.operations.InvokeHostFunctionOperation): InvokeHostFunctionOperation {
        return invokeHostFunctionMapper.mapInvokeHostFunctionOperation(operation)
    }
}