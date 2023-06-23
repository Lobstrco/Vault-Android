package com.lobstr.stellar.vault.domain.tangem

import com.lobstr.stellar.vault.presentation.entities.tangem.TangemError
import com.tangem.operations.sign.SignResponse
import org.stellar.sdk.AbstractTransaction

interface TangemInteractor {
    fun getPublicKeyFromKeyPair(walletPublicKey: ByteArray?): String?

    fun getPublicKeyFromKeyPair(walletPublicKey: String?): ByteArray?

    fun getTransactionFromXDR(xdr: String): AbstractTransaction

    fun signTransactionWithTangemCardData(
        transaction: AbstractTransaction,
        signResponse: SignResponse,
        accountId: String
    ): String?

    fun handleTangemError(error: com.tangem.common.core.TangemError): TangemError?
}