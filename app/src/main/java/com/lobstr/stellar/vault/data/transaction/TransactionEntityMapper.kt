package com.lobstr.stellar.vault.data.transaction

import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionItem
import com.lobstr.stellar.vault.data.net.entities.transaction.ApiTransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.Asset
import com.lobstr.stellar.vault.presentation.entities.transaction.Transaction
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionResult
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.*
import com.lobstr.stellar.vault.presentation.entities.transaction.operation.offer.*
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR
import org.stellar.sdk.AssetTypeCreditAlphaNum12
import org.stellar.sdk.AssetTypeCreditAlphaNum4
import org.stellar.sdk.Memo
import org.stellar.sdk.Network
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
            getTransaction(
                org.stellar.sdk.Transaction.fromEnvelopeXdr(apiTransactionItem.xdr, network)
            )
        )
    }

    fun transformTransactionItem(transaction: org.stellar.sdk.Transaction): TransactionItem {
        return TransactionItem(
            "",
            "",
            transaction.toEnvelopeXdrBase64(),
            "",
            /*transaction?.hash()?.joinToString("") { String.format("%02X", it) } ?:*/ "",
            "",
            IMPORT_XDR,
            null,
            getTransaction(transaction)
        )
    }

    private fun getTransaction(transaction: org.stellar.sdk.Transaction): Transaction {
        val operations: MutableList<Operation> = mutableListOf()

        transaction.operations.forEach {
            when (it) {
                is org.stellar.sdk.PaymentOperation -> operations.add(mapPaymentOperation(transaction.memo, it))
                is org.stellar.sdk.CreateAccountOperation -> operations.add(mapCreateAccountOperation(it))
                is org.stellar.sdk.PathPaymentOperation -> operations.add(mapPathPaymentOperation(it))
                is org.stellar.sdk.ManageSellOfferOperation -> operations.add(mapManageSellOfferOperation(it))
                is org.stellar.sdk.ManageBuyOfferOperation -> operations.add(mapManageBuyOfferOperation(it))
                is org.stellar.sdk.CreatePassiveSellOfferOperation -> operations.add(
                    mapCreatePassiveSellOfferOperation(
                        it
                    )
                )
                is org.stellar.sdk.SetOptionsOperation -> operations.add(mapSetOptionsOperation(it))
                is org.stellar.sdk.ChangeTrustOperation -> operations.add(mapChangeTrustOperation(it))
                is org.stellar.sdk.AllowTrustOperation -> operations.add(mapAllowTrustOperation(it))
                is org.stellar.sdk.AccountMergeOperation -> operations.add(mapAccountMergeOperation(it))
                is org.stellar.sdk.InflationOperation -> operations.add(mapInflationOperation(it))
                is org.stellar.sdk.ManageDataOperation -> operations.add(mapManageDataOperation(it))
                is org.stellar.sdk.BumpSequenceOperation -> operations.add(mapBumpSequenceOperation(it))
            }
        }

        return Transaction(
            transaction.sourceAccount,
            operations,
            transaction.sequenceNumber
        )
    }

    private fun mapPaymentOperation(memo: Memo, operation: org.stellar.sdk.PaymentOperation): PaymentOperation {
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

    private fun mapPathPaymentOperation(operation: org.stellar.sdk.PathPaymentOperation): PathPaymentOperation {
        val path: MutableList<Asset> = mutableListOf()
        if (operation.path.isNotEmpty()) {
            operation.path.forEach {
                path.add(mapAsset(it))
            }
        }

        return PathPaymentOperation(
            (operation as org.stellar.sdk.Operation).sourceAccount,
            mapAsset(operation.sendAsset),
            operation.sendMax,
            operation.destination,
            mapAsset(operation.destAsset),
            operation.destAmount,
            path
        )
    }

    private fun mapManageSellOfferOperation(operation: org.stellar.sdk.ManageSellOfferOperation): ManageSellOfferOperation {
        // determine sell offer operation type: SellOfferOperation or CancelSellOfferOperation
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
            operation.signerWeight
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