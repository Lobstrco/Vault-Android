package com.lobstr.stellar.vault.domain.tangem

import com.lobstr.stellar.vault.domain.stellar.StellarRepository
import com.tangem.operations.sign.SignResponse
import org.stellar.sdk.AbstractTransaction

class TangemInteractorImpl(
    private val stellarRepository: StellarRepository,
    private val tangemRepository: TangemRepository
) : TangemInteractor {

    override fun getPublicKeyFromKeyPair(walletPublicKey: ByteArray?): String? {
        return stellarRepository.getPublicKeyFromKeyPair(walletPublicKey)
    }

    override fun getPublicKeyFromKeyPair(walletPublicKey: String?): ByteArray? {
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

    override fun handleTangemError(error: com.tangem.common.core.TangemError): com.lobstr.stellar.vault.presentation.entities.tangem.TangemError? {
        return tangemRepository.handleError(error)
    }
}