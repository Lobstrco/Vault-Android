package com.lobstr.stellar.tsmapper.data.soroban.data

import com.lobstr.stellar.tsmapper.data.ledger.LedgerMapper
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.data.SorobanData
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.soroban.data.SorobanResources
import org.stellar.sdk.xdr.SorobanTransactionData

class SorobanDataMapper(val ledgerMapper: LedgerMapper = LedgerMapper()) {

    fun mapSorobanData(sorobanData: SorobanTransactionData): SorobanData {
        return SorobanData(
            resources = mapSorobanResources(sorobanData.resources),
            resourceFee = sorobanData.resourceFee.int64.toString()
        )
    }

    private fun mapSorobanResources(resources: org.stellar.sdk.xdr.SorobanResources): SorobanResources {
        return SorobanResources(
            footprint = ledgerMapper.mapLedgerFootprint(resources.footprint),
            instructions = resources.instructions.uint32.number.toString(),
            readBytes = resources.diskReadBytes.uint32.number.toString(),
            writeBytes = resources.writeBytes.uint32.number.toString(),
        )
    }
}