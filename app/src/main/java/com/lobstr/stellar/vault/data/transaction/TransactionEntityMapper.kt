package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.entities.transaction.Transaction
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
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
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.*
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.CreatePassiveSellOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.ManageBuyOfferOperation
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.ManageSellOfferOperation
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR
import org.stellar.sdk.*
import java.math.BigDecimal

class TransactionEntityMapper(private val network: Network) {

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
            apiTransactionItem.xdr,
            apiTransactionItem.signedAt,
            apiTransactionItem.hash!!,
            apiTransactionItem.getStatusDisplay,
            apiTransactionItem.status,
            apiTransactionItem.sequenceOutdatedAt,
            apiTransactionItem.transactionType,
            getTransaction(
                AbstractTransaction.fromEnvelopeXdr(apiTransactionItem.xdr, network)
            )
        )
    }

    fun transformTransactionItem(transaction: AbstractTransaction): TransactionItem {
        return TransactionItem(
            "",
            "",
            transaction.toEnvelopeXdrBase64(),
            "",
            /*transaction?.hash()?.joinToString("") { String.format("%02X", it) } ?:*/ "",
            "",
            IMPORT_XDR,
            null,
            null,
            getTransaction(transaction)
        )
    }

    private fun getTransaction(transaction: AbstractTransaction): Transaction {
        val operations: MutableList<Operation> = mutableListOf()

        val targetTx = when (transaction) {
            is FeeBumpTransaction -> transaction.innerTransaction
            is org.stellar.sdk.Transaction -> transaction
            else -> throw Exception("Unknown transaction type.")
        }

        targetTx.operations.forEach {
            when (it) {
                is org.stellar.sdk.PaymentOperation -> operations.add(
                    mapPaymentOperation(
                        targetTx.memo,
                        it
                    )
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
                is org.stellar.sdk.AllowTrustOperation -> operations.add(mapAllowTrustOperation(it))
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
            }
        }

        return Transaction(
            targetTx.sourceAccount,
            operations,
            targetTx.sequenceNumber
        )
    }

    private fun mapPaymentOperation(
        memo: Memo,
        operation: org.stellar.sdk.PaymentOperation
    ): PaymentOperation {
        return PaymentOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            operation.destination,
            mapAsset(operation.asset),
            operation.amount,
            memo.toString()
        )
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
                KeyPair.fromXdrSignerKey(operation.signer).accountId
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

    private fun mapAsset(asset: org.stellar.sdk.Asset?): Asset {
        return when (asset) {
            is AssetTypeCreditAlphaNum4 -> Asset(asset.code, asset.type, asset.issuer)
            is AssetTypeCreditAlphaNum12 -> Asset(asset.code, asset.type, asset.issuer)
            else -> Asset("XLM", "native", null)
        }
    }
}