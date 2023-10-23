package com.lobstr.stellar.vault.data.tangem

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.tangem.TangemRepository
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemError
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.TangemErrorMod.ERROR_MOD_REPEAT_ACTION
import com.lobstr.stellar.vault.presentation.util.Constant.TangemErrorMod.ERROR_MOD_USER_CANCELLED
import com.tangem.common.core.TangemSdkError

class TangemRepositoryImpl() : TangemRepository {
    override fun handleError(error: com.tangem.common.core.TangemError): TangemError? {
        return when (error) {
            is TangemSdkError.UserCancelled -> {
                TangemError(
                    error.code,
                    ERROR_MOD_USER_CANCELLED,
                    "",
                    ""
                )
            }
            is TangemSdkError.WrongCardNumber -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.tangem_error_wrong_card_title),
                    AppUtil.getString(R.string.tangem_error_wrong_card_description)
                )
            }
            is TangemSdkError.TagLost -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.tangem_error_tag_lost_title),
                    AppUtil.getString(R.string.tangem_error_tag_lost_description)
                )
            }
            // TODO Check duplication.
            is TangemSdkError.UserCancelled -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.tangem_error_tag_lost_title),
                    AppUtil.getString(R.string.tangem_error_tag_lost_description)
                )
            }
            else -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.tangem_error_default_title),
                    AppUtil.getString(R.string.tangem_error_default_description)
                )
            }
        }
    }
}