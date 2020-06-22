package com.lobstr.stellar.vault.presentation.pin

import com.lobstr.stellar.vault.domain.pin.PinInteractor
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dagger.module.pin.PinModule
import javax.inject.Inject

class PinPresenter(private var pinMode: Byte) : BasePresenter<PinView>() {

    @Inject
    lateinit var interactor: PinInteractor

    init {
        LVApplication.appComponent.plusPinComponent(PinModule()).inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showPinFr(pinMode)
    }
}