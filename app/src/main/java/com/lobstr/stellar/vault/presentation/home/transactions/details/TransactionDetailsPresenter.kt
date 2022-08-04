package com.lobstr.stellar.vault.presentation.home.transactions.details

import android.text.format.DateFormat
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.AUTH_CHALLENGE
import com.lobstr.stellar.tsmapper.presentation.util.TsUtil
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.data.error.exeption.*
import com.lobstr.stellar.vault.domain.transaction_details.TransactionDetailsInteractor
import com.lobstr.stellar.vault.domain.util.EventProviderModule
import com.lobstr.stellar.vault.domain.util.event.Auth
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.util.event.Update
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
import com.lobstr.stellar.vault.presentation.util.Constant.Util.PK_TRUNCATE_COUNT_SHORT
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

    private val stellarAccounts: MutableList<Account> = mutableListOf()
    private val cachedStellarAccounts: MutableList<Account> = mutableListOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        registerEventProvider()
        viewState.setupToolbarTitle(
            when (transactionItem.transaction.transactionType) {
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
                    when (it.type) {
                        Update.Type.ACCOUNT_NAME -> {
                            updateAccountNames()
                            checkTransactionInfo()
                        }
                        else -> getTransactionSigners()
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun updateAccountNames() {
        unsubscribeOnDestroy(Single.fromCallable {
            checkAccountNames(stellarAccounts)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { if (it) viewState.notifySignersAdapter(stellarAccounts) },
                Throwable::printStackTrace
            )
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
                        transactionItem.transaction.envelopXdr,
                        targetSourceAccount
                    )
                }
                .doOnSuccess {
                    stellarAccounts.apply {
                        clear()
                        addAll(it.signers)
                    }

                    checkAccountNames(stellarAccounts)

                    // Check cached federation items.
                    stellarAccounts.forEachIndexed { index, accountItem ->
                        val federation =
                            cachedStellarAccounts.find { account -> account.address == accountItem.address }
                                ?.federation
                        stellarAccounts[index].federation = federation
                    }
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
                    viewState.showSignersContainer(stellarAccounts.isNotEmpty())
                    calculateSignersCount(it)
                    viewState.notifySignersAdapter(stellarAccounts)
                    // Try receive federations for accounts.
                    getStellarAccounts(stellarAccounts)
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

    private fun calculateSignersCount(accountResult: AccountResult) {
        var countSummaryStr: String? = null
        var countToSubmitStr: String? = null

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

                if (isThresholdsSameAndNotZero and isWeightsSameAndNotZero) {
                    val countToConfirm =
                        kotlin.math.ceil((highThreshold.toFloat() / accountResult.signers.first().weight!!.toFloat()))
                            .toInt()

                    countSummaryStr = "${accountResult.signers.filter { it.signed == true }.size} ${
                        AppUtil.getString(
                            R.string.text_tv_of
                        )
                    } $countToConfirm"

                    // Show signatures count to submit additional info only for non AUTH_CHALLENGE transactions.
                    if (transactionItem.transaction.transactionType != AUTH_CHALLENGE) {
                        countToSubmitStr = AppUtil.getQuantityString(
                            R.plurals.text_tv_signatures_count_to_submit,
                            countToConfirm,
                            countToConfirm
                        )
                    }
                }
            }
        }

        viewState.showSignersCount(countSummaryStr, countToSubmitStr)
    }

    /**
     * Check Accounts' names from cache.
     * @return true when Accounts' names was changed.
     */
    private fun checkAccountNames(accounts: List<Account>): Boolean {
        val names = interactor.getAccountNames()
        var accountNamesChanged = false
        for (account in accounts) {
            val name = names[account.address]
            if (!accountNamesChanged) accountNamesChanged = account.name != name
            account.name = name
        }

        return accountNamesChanged
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
            viewState.initOperationList(
                when (transactionItem.transaction.transactionType) {
                    AUTH_CHALLENGE -> R.string.text_transaction_challenge
                    else -> R.string.title_toolbar_transaction_details
                },
                mutableListOf<Int>().apply {
                    // Prepare operations list for show it.
                    for (operation in transactionItem.transaction.operations) {
                        val resId: Int =
                            TsUtil.getTransactionOperationName(operation)
                        if (resId != -1) {
                            add(resId)
                        }
                    }
                })
        } else {
            prepareOperationDetails(0, isInitState = true)
        }

        // Check and setup additional info like Source Account and date.
        checkTransactionInfo()
    }

    fun operationDetailsClicked(opPosition: Int) {
        prepareOperationDetails(opPosition, isInitState = false)
    }

    /**
     * Prepare Operation Details screen.
     * @param opPosition Operation position in the list.
     * @param isInitState Single or 'From Operations List' state.
     */
    private fun prepareOperationDetails(opPosition: Int, isInitState: Boolean) {
        transactionItem.transaction.operations.getOrNull(opPosition)?.let { operation ->
            val title = when {
                transactionItem.transaction.operations.size == 1 && transactionItem.transaction.transactionType == AUTH_CHALLENGE -> {
                    R.string.text_transaction_challenge // Specific case for the 'Single Operation' Challenge Transaction.
                }
                else -> Constant.Util.UNDEFINED_VALUE
            }
            if (isInitState) {
                viewState.initOperationDetailsScreen(
                    title,
                    operation,
                    transactionItem.transaction.sourceAccount
                )
            } else {
                viewState.showOperationDetailsScreen(
                    title,
                    operation,
                    transactionItem.transaction.sourceAccount
                )
            }
        }
    }

    private fun checkTransactionInfo() {
        unsubscribeOnDestroy(
            Single.fromCallable {
                val fields = mutableListOf<OperationField>()

                val memo = transactionItem.transaction.memo
                val sourceAccount = transactionItem.transaction.sourceAccount
                val addedAt = transactionItem.addedAt

                if (!memo.value.isNullOrEmpty()) {
                    val memoTitle = TsUtil.getMemoTypeStr(AppUtil.getAppContext(), memo).run {
                        if (isEmpty()) AppUtil.getString(R.string.text_tv_transaction_memo) else "${AppUtil.getString(R.string.text_tv_transaction_memo)} ($this)"
                    }

                    fields.add(OperationField(memoTitle, memo.value))
                }

                if (sourceAccount.isNotEmpty()) {
                    fields.add(OperationField(AppUtil.getString(R.string.text_tv_transaction_source_account),
                        interactor.getAccountNames()[sourceAccount]?.plus(" (${AppUtil.ellipsizeStrInMiddle(sourceAccount, PK_TRUNCATE_COUNT_SHORT)})") ?: sourceAccount, sourceAccount))
                }

                if (!addedAt.isNullOrEmpty()) {
                    fields.add(OperationField(AppUtil.getString(R.string.text_tv_added_at), AppUtil.formatDate(
                        ZonedDateTime.parse(addedAt).toInstant().toEpochMilli(),
                        if (DateFormat.is24HourFormat(AppUtil.getAppContext())) "MMM dd yyyy HH:mm" else "MMM dd yyyy h:mm a"
                    )))
                }

                return@fromCallable fields
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { viewState.setupTransactionInfo(it) },
                    Throwable::printStackTrace
                )
        )
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
        viewState.copyToClipBoard(transactionItem.transaction.envelopXdr)
    }

    fun copySignedXdrClicked() {
        signTransaction()
    }

    private fun signTransaction() {
        when {
            interactor.hasMnemonics() -> {
                unsubscribeOnDestroy(
                    interactor.signTransaction(transactionItem.transaction.envelopXdr)
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
                        pendingTransaction = transactionItem.transaction.envelopXdr
                    }
                )
            }
        }
    }

    fun viewTransactionDetailsClicked() {
        viewState.showWebPage(AppUtil.composeLaboratoryUrl(transactionItem.transaction.envelopXdr))
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
                                    pendingTransaction = it.transaction.envelopXdr
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
     * 2. Is [com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.AUTH_CHALLENGE] transaction type -> retrieve actual transaction -> sign it and notify vault server
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
                        interactor.signTransaction(it.transaction.envelopXdr)
                    } else {
                        // Case for transaction received via Tangem.
                        interactor.createTransaction(signedTransaction)
                    }
                }
                .flatMap {
                    when (transactionItem.transaction.transactionType) {
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

                                    val errorToShow = when (transactionResultCode == "tx_failed") {
                                        true -> {
                                            // tx_failed - one of the operations failed (none were applied). Show first error and exclude op_success.
                                            operationResultCodes?.firstOrNull { code -> code != "op_success" } ?: transactionResultCode
                                        }
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
                                if (transactionItem.transaction.transactionType == AUTH_CHALLENGE) {
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

    fun signedAccountItemClicked(account: Account) {
        viewState.showEditAccountDialog(account.address)
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

    /**
     * @param key Reserved for future implementations.
     * @param value Reserved for future implementations.
     * @param tag Additional info for field (e.g. Asset for asset code)
     */
    fun additionalInfoValueClicked(key: String, value: String?, tag: Any?) {
        tag?.let { if (AppUtil.isValidAccount(tag as? String)) viewState.showEditAccountDialog(tag as String) }
    }
}