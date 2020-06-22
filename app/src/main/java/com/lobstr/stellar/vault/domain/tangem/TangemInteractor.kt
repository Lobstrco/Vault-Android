package com.lobstr.stellar.vault.domain.tangem

import com.lobstr.stellar.vault.presentation.entities.tangem.TangemError
import com.tangem.TangemSdkError
import com.tangem.commands.SignResponse
import org.stellar.sdk.AbstractTransaction

interface TangemInteractor {
    fun getPublicKeyFromKeyPair(walletPublicKey: ByteArray?): String?

    fun getTransactionFromXDR(xdr: String): AbstractTransaction

    fun signTransactionWithTangemCardData(
        transaction: AbstractTransaction,
        signResponse: SignResponse,
        accountId: String
    ): String?

    fun handleTangemError(error: TangemSdkError): TangemError?
}