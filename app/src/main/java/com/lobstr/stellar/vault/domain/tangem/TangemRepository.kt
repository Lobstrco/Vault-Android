package com.lobstr.stellar.vault.domain.tangem

import com.lobstr.stellar.vault.presentation.entities.tangem.TangemError


interface TangemRepository {

    fun handleError(error: com.tangem.common.core.TangemError): TangemError?
}