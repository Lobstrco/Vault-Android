package com.lobstr.stellar.vault.data.tangem

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.tangem.TangemRepository
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemError
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant.TangemErrorMod.ERROR_MOD_REPEAT_ACTION
import com.tangem.TangemSdkError

class TangemRepositoryImpl() : TangemRepository {
    override fun handleError(error: TangemSdkError): TangemError? {
        return when (error) {
            is TangemSdkError.WrongCardNumber -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.text_tv_tangem_wrong_card_error_header),
                    AppUtil.getString(R.string.text_tv_tangem_wrong_card_error_description)
                )
            }
            is TangemSdkError.TagLost -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.text_tv_tangem_tag_lost_error_header),
                    AppUtil.getString(R.string.text_tv_tangem_tag_lost_error_description)
                )
            }
            is TangemSdkError.UserCancelled -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.text_tv_tangem_tag_lost_error_header),
                    AppUtil.getString(R.string.text_tv_tangem_tag_lost_error_description)
                )
            }
            else -> {
                TangemError(
                    error.code,
                    ERROR_MOD_REPEAT_ACTION,
                    AppUtil.getString(R.string.text_tv_tangem_default_error_header),
                    AppUtil.getString(R.string.text_tv_tangem_default_error_description)
                )
            }
        }
    }
}