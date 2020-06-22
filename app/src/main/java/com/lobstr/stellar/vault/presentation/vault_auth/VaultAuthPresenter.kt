package com.lobstr.stellar.vault.presentation.vault_auth

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.vault_auth_screen.VaultAuthInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.vault_auth.VaultAuthModule
import javax.inject.Inject

class VaultAuthPresenter : BasePresenter<VaultAuthView>() {

    @Inject
    lateinit var interactor: VaultAuthInteractor

    @Inject
    lateinit var eventProviderModule: EventProviderModule

    init {
        LVApplication.appComponent.plusVaultAuthComponent(VaultAuthModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbar(
            android.R.color.white,
            R.drawable.ic_arrow_back,
            R.color.color_primary
        )

        viewState.showAuthTokenFragment()
    }
}