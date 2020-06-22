package com.lobstr.stellar.vault.domain.tangem

import com.lobstr.stellar.vault.presentation.entities.tangem.TangemError
import com.tangem.TangemSdkError

interface TangemRepository {

    fun handleError(error: TangemSdkError): TangemError?
}