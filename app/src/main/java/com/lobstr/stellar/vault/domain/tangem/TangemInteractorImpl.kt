package com.lobstr.stellar.vault.domain.tangem

import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemError
import com.tangem.TangemSdkError
import com.tangem.commands.SignResponse
import org.stellar.sdk.AbstractTransaction

class TangemInteractorImpl(
    private val stellarRepository: StellarRepository,
    private val tangemRepository: TangemRepository
) : TangemInteractor {

    override fun getPublicKeyFromKeyPair(walletPublicKey: ByteArray?): String? {
        return stellarRepository.getPublicKeyFromKeyPair(walletPublicKey)
    }

    override fun getTransactionFromXDR(xdr: String): AbstractTransaction {
        return stellarRepository.getTransactionFromXDR(xdr)
    }

    override fun signTransactionWithTangemCardData(
        transaction: AbstractTransaction,
        signResponse: SignResponse,
        accountId: String
    ): String? {
        return stellarRepository.signTransactionWithTangemCardData(
            transaction,
            signResponse,
            accountId
        )
    }

    override fun handleTangemError(error: TangemSdkError): TangemError? {
        return tangemRepository.handleError(error)
    }
}