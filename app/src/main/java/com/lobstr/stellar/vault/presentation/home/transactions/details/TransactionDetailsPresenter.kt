package com.lobstr.stellar.vault.presentation.home.transactions.details

import android.text.format.DateFormat
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.*
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Auth
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.presentation.BasePresenter
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.CONFIRM_TRANSACTION
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.DENY_TRANSACTION
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.CANCELLED
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.IMPORT_XDR
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.PENDING
import com.lobstr.stellar.vault.presentation.util.Constant.Transaction.SIGNED
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS_CHALLENGE
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS_NEED_ADDITIONAL_SIGNATURES
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionType.Item.AUTH_CHALLENGE
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.ZonedDateTime
import javax.inject.Inject

class TransactionDetailsPresenter @Inject constructor(
    private val interactor: TransactionDetailsInteractor,
    private val eventProviderModule: EventProviderModule
) : BasePresenter<TransactionDetailsView>() {

    lateinit var transactionItem: TransactionItem

    private var confirmationInProcess = false
    private var cancellationInProcess = false

    // Used for dividing Tangem action states in handleTangemInfo() method: true - Just Sign Transaction, false - confirm).
    private var tangemSignTransactionState = false

    private var stellarAccountsDisposable: Disposable? = null
    private val cachedStellarAccounts: MutableList<Account> = mutableListOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        registerEventProvider()
        viewState.setupToolbarTitle(
            when (transactionItem.transactionType) {
                AUTH_CHALLENGE -> R.string.text_transaction_challenge
                else -> R.string.title_toolbar_transaction_details
            }
        )
        viewState.initSignersRecycledView()
        prepareUiAndOperationsList()
        getTransactionSigners()
    }

    private fun registerEventProvider() {
        unsubscribeOnDestroy(
            eventProviderModule.networkEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.type) {
                        Network.Type.CONNECTED -> {
                            if (needCheckConnectionState) {
                                getTransactionSigners()
                            }
                            cancelNetworkWorker(false)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )

        unsubscribeOnDestroy(
            eventProviderModule.updateEventSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getTransactionSigners()
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun getTransactionSigners() {
        unsubscribeOnDestroy(
            interactor.getSignedAccounts()
                .flatMap {
                    // Check Target Source Account (by default transaction.sourceAccount).
                    var targetSourceAccount = transactionItem.transaction.sourceAccount

                    // First - try to find transaction.sourceAccount in the signed accounts list. If don't exist - try to find an operations source account in it.
                    if (it.find { account -> account.address == targetSourceAccount } == null) {
                        for (operation in transactionItem.transaction.operations) {
                            if (it.find { account -> account.address == operation.sourceAccount } != null) {
                                targetSourceAccount = operation.sourceAccount!!
                            }
                        }
                    }

                    interactor.getTransactionSigners(
                        transactionItem.xdr!!,
                        targetSourceAccount
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showSignersContainer(false)
                    viewState.showSignersProgress(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showSignersProgress(false)
                }
                .subscribe({
                    viewState.showSignersContainer(it.signers.isNotEmpty())
                    viewState.showSignersCount(calculateSignersCount(it))

                    // Check cached federation items.
                    it.signers.forEachIndexed { index, accountItem ->
                        val federation =
                            cachedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        it.signers[index].federation = federation
                    }
                    viewState.notifySignersAdapter(it.signers)

                    // Try receive federations for accounts.
                    getStellarAccounts(it.signers)
                }, {
                    when (it) {
                        is NoInternetConnectionException -> {
                            viewState.showMessage(it.details)
                            handleNoInternetConnection()
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> getTransactionSigners()
                            }
                        }
                    }
                })
        )
    }

    private fun calculateSignersCount(accountResult: AccountResult): String? {
        when {
            accountResult.signers.isNotEmpty() -> {

                // Only check the case when thresholds and weights is the same and don't equals 0.

                val lowThreshold = accountResult.thresholds.lowThreshold
                val medThreshold = accountResult.thresholds.medThreshold
                val highThreshold = accountResult.thresholds.highThreshold

                val isThresholdsSameAndNotZero =
                    (lowThreshold != 0) && (lowThreshold == medThreshold) && (lowThreshold == highThreshold)
                val isWeightsSameAndNotZero =
                    accountResult.signers.first().weight != 0 && accountResult.signers.filter { it.weight == accountResult.signers.first().weight }.size == accountResult.signers.size

                return if (isThresholdsSameAndNotZero and isWeightsSameAndNotZero) {
                    "${accountResult.signers.filter { it.signed == true }.size} ${
                        AppUtil.getString(
                            R.string.text_tv_of
                        )
                    } ${
                        kotlin.math.ceil((highThreshold.toFloat() / accountResult.signers.first().weight!!.toFloat()))
                            .toInt()
                    }"
                } else {
                    null
                }
            }
            else -> {
                return null
            }
        }
    }

    /**
     * Used for receive federation by account id.
     */
    private fun getStellarAccounts(accounts: List<Account>) {
        stellarAccountsDisposable?.dispose()
        stellarAccountsDisposable = Observable.fromIterable(accounts)
            .subscribeOn(Schedulers.io())
            .filter { account: Account ->
                cachedStellarAccounts
                    .find { cachedAccount -> cachedAccount.address == account.address } == null
            }
            .flatMapSingle {
                interactor.getStellarAccount(it.address).onErrorReturnItem(it)
            }
            .filter { it.federation != null }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.forEach { account ->
                    if (cachedStellarAccounts.find { cachedAccount -> cachedAccount.address == account.address } == null) {
                        cachedStellarAccounts.add(account)
                    }
                }

                // Check cached federation items.
                accounts.forEachIndexed { index, accountItem ->
                    val federation =
                        cachedStellarAccounts.find { account -> account.address == accountItem.address }
                            ?.federation
                    accounts[index].federation = federation
                }

                viewState.notifySignersAdapter(accounts)
            }, {
                // Ignore.
            })

        unsubscribeOnDestroy(stellarAccountsDisposable!!)
    }

    private fun prepareUiAndOperationsList() {
        // Handle transaction validation status.
        viewState.setTransactionValid(transactionItem.sequenceOutdatedAt.isNullOrEmpty())

        // Handle transaction status.
        when (transactionItem.status) {
            PENDING -> viewState.setActionBtnVisibility(true, true)
            CANCELLED -> viewState.setActionBtnVisibility(false, false)
            SIGNED -> viewState.setActionBtnVisibility(false, false)
            IMPORT_XDR -> viewState.setActionBtnVisibility(true, true)
        }

        // Prepare operations list or show single operation:
        // when transaction has only one operation - show operation details screen immediately, else - list.
        if (transactionItem.transaction.operations.size > 1) {
            viewState.showOperationList(transactionItem)
        } else {
            viewState.showOperationDetailsScreen(transactionItem, 0)
        }

        // Check and setup additional info like Source Account and date.
        checkTransactionInfo()
    }

    private fun checkTransactionInfo() {
        val map: MutableMap<String, String?> = mutableMapOf()

        val memo = transactionItem.transaction.memo
        val sourceAccount = transactionItem.transaction.sourceAccount
        val addedAt = transactionItem.addedAt

        if (!memo.isNullOrEmpty()) {
            map[AppUtil.getString(R.string.text_tv_transaction_memo)] = memo
        }

        if (!sourceAccount.isNullOrEmpty()) {
            map[AppUtil.getString(R.string.text_tv_transaction_source_account)] =
                AppUtil.ellipsizeStrInMiddle(sourceAccount, PK_TRUNCATE_COUNT)
        }

        if (!addedAt.isNullOrEmpty()) {
            map[AppUtil.getString(R.string.text_tv_added_at)] =
                AppUtil.formatDate(
                    ZonedDateTime.parse(addedAt).toInstant().toEpochMilli(),
                    if (DateFormat.is24HourFormat(AppUtil.getAppContext())) "MMM dd yyyy HH:mm" else "MMM dd yyyy h:mm a"
                )
        }

        viewState.setupTransactionInfo(map)
    }

    fun handleTangemInfo(tangemInfo: TangemInfo?) {
        if (tangemInfo != null) {
            if (tangemSignTransactionState) {
                // Save signed xdr to clipboard.
                viewState.copyToClipBoard(tangemInfo.signedTransaction!!)
            } else {
                // Confirm transaction after Tangem action.
                confirmTransaction(tangemInfo.signedTransaction)
            }
        }
    }

    fun copyXdrClicked() {
        viewState.copyToClipBoard(transactionItem.xdr!!)
    }

    fun copySignedXdrClicked() {
        signTransaction()
    }

    private fun signTransaction() {
        when {
            interactor.hasMnemonics() -> {
                unsubscribeOnDestroy(
                    interactor.signTransaction(transactionItem.xdr!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            // Save signed xdr to clipboard.
                            viewState.copyToClipBoard(it.toEnvelopeXdrBase64())
                        }, {
                            // Ignore errors.
                        })
                )
            }
            interactor.hasTangem() -> {
                // Change state for Tangem action - sign.
                tangemSignTransactionState = true
                viewState.showTangemScreen(
                    TangemInfo().apply {
                        accountId = interactor.getUserPublicKey()
                        cardId = interactor.getTangemCardId()
                        pendingTransaction = transactionItem.xdr
                    }
                )
            }
        }
    }

    fun viewTransactionDetailsClicked() {
        viewState.showWebPage(AppUtil.composeLaboratoryUrl(transactionItem.xdr!!))
    }

    fun btnConfirmClicked() {
        when {
            interactor.hasMnemonics() -> {
                when {
                    interactor.isTrConfirmationEnabled() -> viewState.showConfirmTransactionDialog()
                    else -> confirmTransaction()
                }
            }
            interactor.hasTangem() -> {
                // Change state for Tangem action - confirm.
                tangemSignTransactionState = false
                if (confirmationInProcess) {
                    return
                }

                unsubscribeOnDestroy(
                    interactor.retrieveActualTransaction(transactionItem)
                        .subscribeOn(Schedulers.io())
                        .doOnSuccess {
                            transactionItem = it
                            when (transactionItem.status) {
                                CANCELLED, SIGNED -> throw DefaultException(AppUtil.getString(R.string.msg_transaction_already_signed_or_denied))
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            confirmationInProcess = true
                            viewState.showProgressDialog(true)
                        }
                        .doOnEvent { _, _ ->
                            confirmationInProcess = false
                            viewState.showProgressDialog(false)
                        }
                        .subscribe({
                            viewState.showTangemScreen(
                                TangemInfo().apply {
                                    accountId = interactor.getUserPublicKey()
                                    cardId = interactor.getTangemCardId()
                                    pendingTransaction = it.xdr
                                }
                            )
                        }, {
                            when (it) {
                                is UserNotAuthorizedException -> {
                                    when (it.action) {
                                        UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                            Auth()
                                        )
                                    }
                                }
                                is DefaultException -> {
                                    viewState.showMessage(it.details)
                                }
                                else -> {
                                    viewState.showMessage(it.message ?: "")
                                }
                            }
                        })
                )
            }
        }
    }

    fun btnDenyClicked() {
        when {
            interactor.hasMnemonics() -> {
                when {
                    interactor.isTrConfirmationEnabled() -> viewState.showDenyTransactionDialog()
                    else -> denyTransaction()
                }
            }
            interactor.hasTangem() -> {
                denyTransaction()
            }
        }
    }

    /**
     * Cases:
     * 1. Is Vault Transaction -> retrieve actual transaction -> sign it on horizon -> notify vault server
     * 2. Is [Constant.TransactionType.Item.AUTH_CHALLENGE] transaction type -> retrieve actual transaction -> sign it and notify vault server
     * 3. Is Transaction from IMPORT_XDR: only sign it on horizon
     * 4. Is Transaction after Tangem sign
     */
    private fun confirmTransaction(signedTransaction: String? = null) {
        if (confirmationInProcess) {
            return
        }

        // Nullable state - undefined state for handle transaction type AUTH_CHALLENGE behavior.
        var needAdditionalSignatures: Boolean? = null

        var hash: String? = transactionItem.hash

        unsubscribeOnDestroy(
            interactor.retrieveActualTransaction(
                transactionItem,
                !signedTransaction.isNullOrEmpty()
            )
                .subscribeOn(Schedulers.io())
                .flatMap {
                    transactionItem = it

                    when (transactionItem.status) {
                        CANCELLED, SIGNED -> throw DefaultException(AppUtil.getString(R.string.msg_transaction_already_signed_or_denied))
                    }

                    if (signedTransaction.isNullOrEmpty()) {
                        // Case for transaction received via Mnemonics.
                        interactor.signTransaction(it.xdr!!)
                    } else {
                        // Case for transaction received via Tangem.
                        interactor.createTransaction(signedTransaction)
                    }
                }
                .flatMap {
                    when (transactionItem.transactionType) {
                        AUTH_CHALLENGE -> {
                            needAdditionalSignatures = null
                            Single.fromCallable { it.toEnvelopeXdrBase64() }
                        }
                        else -> {
                            needAdditionalSignatures = false
                            interactor.confirmTransactionOnHorizon(it)
                                .flatMap { submitTransactionResponse ->
                                    val envelopXdr = submitTransactionResponse.envelopeXdr.get()
                                    val extras = submitTransactionResponse.extras

                                    val transactionResultCode =
                                        extras?.resultCodes?.transactionResultCode

                                    val operationResultCodes =
                                        extras?.resultCodes?.operationsResultCodes

                                    val errorToShow =
                                        when (val operationResultCode =
                                            if (operationResultCodes.isNullOrEmpty()) null else operationResultCodes.first()) {
                                            // Handle specific operation result code:
                                            // op_underfunded - used to specify the operation failed due to a lack of funds.
                                            "op_underfunded" -> operationResultCode
                                            else -> transactionResultCode
                                        }

                                    when {
                                        envelopXdr == null -> throw HorizonException(
                                            errorToShow!!,
                                            it.toEnvelopeXdrBase64()
                                        )
                                        transactionResultCode != null && transactionResultCode != "tx_bad_auth" -> throw HorizonException(
                                            errorToShow!!,
                                            it.toEnvelopeXdrBase64()
                                        )
                                        transactionResultCode != null && transactionResultCode == "tx_bad_auth" -> needAdditionalSignatures =
                                            true
                                    }

                                    hash = submitTransactionResponse.hash
                                    Single.fromCallable { envelopXdr }
                                }
                        }
                    }
                }
                .flatMap {
                    interactor.confirmTransactionOnServer(
                        needAdditionalSignatures ?: true,
                        transactionItem.status,
                        hash,
                        it
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    confirmationInProcess = true
                    viewState.showProgressDialog(true)
                }
                .doOnEvent { _, _ ->
                    viewState.showProgressDialog(false)
                    confirmationInProcess = false
                }
                .subscribe({
                    // Update transaction status.
                    transactionItem.status = SIGNED
                    viewState.successConfirmTransaction(
                        it,
                        when (needAdditionalSignatures) {
                            true -> SUCCESS_NEED_ADDITIONAL_SIGNATURES
                            false -> SUCCESS
                            else -> {
                                if (transactionItem.transactionType == AUTH_CHALLENGE) {
                                    SUCCESS_CHALLENGE
                                } else {
                                    SUCCESS
                                }
                            }
                        },
                        transactionItem
                    )
                    // Notify about transaction changed.
                    eventProviderModule.notificationEventSubject.onNext(
                        Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                    )
                }, {
                    when (it) {
                        is HorizonException -> {
                            viewState.errorConfirmTransaction(it.details, it.xdr)
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> confirmTransaction()
                            }
                        }
                        is InternalException -> viewState.showMessage(
                            AppUtil.getString(
                                R.string.api_error_internal_submit_transaction
                            )
                        )
                        is DefaultException -> {
                            viewState.showMessage(it.details)
                        }
                        else -> {
                            viewState.showMessage(it.message ?: "")
                        }
                    }
                })
        )
    }

    private fun denyTransaction() {
        // Check transaction status.
        when (transactionItem.status) {
            // IMPORT_XDR - 'success deny transaction' action without api call.
            IMPORT_XDR -> {
                viewState.successDenyTransaction(transactionItem)

                // Notify about transaction changed.
                eventProviderModule.notificationEventSubject.onNext(
                    Notification(Notification.Type.TRANSACTION_COUNT_CHANGED, null)
                )
            }
            else -> {
                if (cancellationInProcess) {
                    return
                }
                unsubscribeOnDestroy(
                    interactor.cancelTransaction(transactionItem.hash)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            cancellationInProcess = true
                            viewState.showProgressDialog(true)
                        }
                        .doOnEvent { _, _ ->
                            viewState.showProgressDialog(false)
                            cancellationInProcess = false
                        }
                        .subscribe({
                            transactionItem = it
                            viewState.successDenyTransaction(it)
                            // Notify about transaction changed.
                            eventProviderModule.notificationEventSubject.onNext(
                                Notification(
                                    Notification.Type.TRANSACTION_COUNT_CHANGED,
                                    null
                                )
                            )
                        }, {
                            when (it) {
                                is UserNotAuthorizedException -> {
                                    when (it.action) {
                                        UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                            Auth()
                                        )
                                        else -> denyTransaction()
                                    }
                                }
                                is InternalException -> viewState.showMessage(
                                    AppUtil.getString(
                                        R.string.api_error_internal_submit_transaction
                                    )
                                )
                                is HttpNotFoundException -> {
                                    viewState.showMessage(AppUtil.getString(R.string.msg_transaction_already_signed_or_denied))
                                }
                                is DefaultException -> viewState.showMessage(it.details)
                                else -> {
                                    viewState.showMessage(it.message ?: "")
                                }
                            }
                        })
                )
            }
        }
    }

    fun onAlertDialogPositiveButtonClicked(tag: String?) {
        when (tag) {
            DENY_TRANSACTION -> denyTransaction()
            CONFIRM_TRANSACTION -> confirmTransaction()
        }
    }

    fun signedAccountItemLongClicked(account: Account) {
        viewState.copyToClipBoard(account.address)
    }

    /**
     * Used for handle action container visibility after click on operation from operations list.
     * Hide action container after click on operation details.
     */
    fun backStackChanged(backStackEntryCount: Int) {
        viewState.showActionContainer(backStackEntryCount == 1)
    }
}