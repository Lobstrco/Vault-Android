package com.lobstr.stellar.vault.presentation.container.activity

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.entities.error.Error
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.CONFIG
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.ERROR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS
import moxy.MvpPresenter

class ContainerPresenter(
    private val targetFr: Int,
    private val transactionItem: TransactionItem?,
    private val envelopeXdr: String?,
    private val transactionConfirmationSuccessStatus: Byte?,
    private val error: Error?,
    private val config: Int
) : MvpPresenter<ContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary,
            android.R.color.black
        )

        viewState.showContainerFr(*createArgs(targetFr))
    }

    private fun createArgs(targetFr: Int): Array<Pair<String, Any?>> {
        return mutableListOf<Pair<String, Any?>>().apply {
            add(Pair(Constant.Bundle.BUNDLE_NAVIGATION_FR, targetFr))
            // Apply specific values here.
            when (targetFr) {
                TRANSACTION_DETAILS -> add(
                    Pair(
                        Constant.Bundle.BUNDLE_TRANSACTION_ITEM,
                        transactionItem
                    )
                )
                SUCCESS -> {
                    add(Pair(Constant.Bundle.BUNDLE_ENVELOPE_XDR, envelopeXdr))
                    add(
                        Pair(
                            Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
                            transactionConfirmationSuccessStatus
                        )
                    )
                }
                ERROR -> add(Pair(Constant.Bundle.BUNDLE_ERROR, error))
                CONFIG -> add(Pair(Constant.Bundle.BUNDLE_CONFIG, config))
            }
        }.toTypedArray()
    }
}