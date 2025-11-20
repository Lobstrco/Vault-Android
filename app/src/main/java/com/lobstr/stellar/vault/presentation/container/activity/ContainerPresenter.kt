package com.lobstr.stellar.vault.presentation.container.activity

import android.content.Intent
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.CONFIG
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.ERROR
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.Navigation.TRANSACTION_DETAILS
import com.lobstr.stellar.vault.presentation.util.parcelableExtra
import moxy.MvpPresenter

class ContainerPresenter(
    private val intent: Intent
) : MvpPresenter<ContainerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary,
            android.R.color.black
        )

        viewState.showContainerFr(
            *createArgs(
                intent.getIntExtra(
                    Constant.Extra.EXTRA_NAVIGATION_FR,
                    Constant.Navigation.DASHBOARD
                )
            )
        )
    }

    private fun createArgs(targetFr: Int): Array<Pair<String, Any?>> {
        return mutableListOf<Pair<String, Any?>>().apply {
            add(Pair(Constant.Bundle.BUNDLE_NAVIGATION_FR, targetFr))
            // Apply specific values here.
            when (targetFr) {
                TRANSACTION_DETAILS -> {
                    add(
                        Pair(
                            Constant.Bundle.BUNDLE_TRANSACTION_ITEM,
                            intent.parcelableExtra(Constant.Extra.EXTRA_TRANSACTION_ITEM)
                        )
                    )
                    add(
                        Pair(
                            Constant.Bundle.BUNDLE_TRANSACTION_HASH,
                            intent.getStringExtra(Constant.Extra.EXTRA_TRANSACTION_HASH)
                        )
                    )
                }
                SUCCESS -> {
                    add(Pair(Constant.Bundle.BUNDLE_ENVELOPE_XDR, intent.getStringExtra(Constant.Extra.EXTRA_ENVELOPE_XDR)))
                    add(
                        Pair(
                            Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
                            intent.getByteExtra(
                                Constant.Extra.EXTRA_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
                                Constant.TransactionConfirmationSuccessStatus.SUCCESS
                            )
                        )
                    )
                }
                ERROR -> add(Pair(Constant.Bundle.BUNDLE_ERROR, intent.parcelableExtra(Constant.Extra.EXTRA_ERROR)))
                CONFIG -> add(Pair(Constant.Bundle.BUNDLE_CONFIG, intent.getIntExtra(Constant.Extra.EXTRA_CONFIG,
                    Constant.Util.UNDEFINED_VALUE
                )))
            }
        }.toTypedArray()
    }
}