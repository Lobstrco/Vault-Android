package com.lobstr.stellar.vault.presentation.home.account_name.add

import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.domain.account_name.add.AddAccountNameInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.util.AppUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AddAccountNamePresenter @Inject constructor(
    private val interactor: AddAccountNameInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<AddAccountNameView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setupToolbarTitle(R.string.toolbar_add_account_name_title)
    }

    fun saveClicked(address: String, name: String) {
        val isValidAddress = validateAddress(address)
        val isValidName = validateName(name)

        if (isValidAddress && isValidName) {
            Completable.fromCallable {
                interactor.saveAccountName(address, name)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        // Notify about update content event.
                        eventProviderModule.updateEventSubject.onNext(Update(Update.Type.ACCOUNT_NAME))
                        viewState.closeScreen()
                    },
                    Throwable::printStackTrace
                )
        }
    }

    private fun validateAddress(address: String): Boolean {
        val isValidAddress = address.isNotEmpty() && AppUtil.isPublicKey(address)
        viewState.showAddressFormError(!isValidAddress)
        return isValidAddress
    }

    private fun validateName(name: String): Boolean {
        val isValidName = name.isNotEmpty()
        viewState.showNameFormError(!isValidName)
        return isValidName
    }

    fun addressChanged(addressLength: Int, nameLength: Int) {
        viewState.showAddressFormError(false)
        viewState.setSaveEnabled(addressLength != 0 && nameLength != 0)
    }

    fun nameChanged(nameLength: Int, addressLength: Int) {
        viewState.showNameFormError(false)
        viewState.setSaveEnabled(addressLength != 0 && nameLength != 0)
    }
}