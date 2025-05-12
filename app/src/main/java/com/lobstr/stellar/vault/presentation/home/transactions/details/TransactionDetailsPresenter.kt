package com.lobstr.stellar.vault.presentation.home.transactions.details

import android.text.format.DateFormat
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.asset.Asset
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.OperationField
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TxResultCode.Code.TX_BAD_AUTH
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TxResultCode.Code.TX_FAILED
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TxResultCode.Code.TX_FEE_BUMP_INNER_SUCCESS
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.TxResultCode.Code.TX_SUCCESS
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation.OpResultCode.Code.OP_BAD_AUTH
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation.OpResultCode.Code.OP_INNER
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.result.operation.OpResultCode.Code.OP_SUCCESS
import com.lobstr.stellar.tsmapper.presentation.util.Constant.TransactionType.AUTH_CHALLENGE
import com.lobstr.stellar.tsmapper.presentation.util.Constant.Util.UNDEFINED_VALUE
import com.lobstr.stellar.tsmapper.presentation.util.Constant.XLM
import com.lobstr.stellar.tsmapper.presentation.util.Constant.XLM.BASE_FEE
import com.lobstr.stellar.tsmapper.presentation.util.Constant.XLM.STROOP
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
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.DECLINE_TRANSACTION
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogFragmentIdentifier.SEQUENCE_NUMBER_WARNING
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.entities.account.AccountResult
import com.lobstr.stellar.vault.presentation.entities.error.Error
import com.lobstr.stellar.vault.presentation.entities.stellar.SorobanBalanceData
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
import com.lobstr.stellar.vault.presentation.util.manager.composeViewXdrUrl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
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

    // (Count To Confirm, Signed Count, is Vault Account Pending).
    private var signersCountInfo: Triple<Int, Int, Boolean>? = null

    var sorobanBalanceChanges: List<SorobanBalanceData>? = null

    // Pre-saved scroll state for the main content.
    private var initialScrollX = 0
    private var initialScrollY = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        registerEventProvider()
        viewState.setupToolbarTitle(
            when (transactionItem.transaction.transactionType) {
                AUTH_CHALLENGE -> R.string.transaction_details_challenge_title
                else -> R.string.toolbar_transaction_details_title
            }
        )
        viewState.initSignersRecycledView()
        prepareUiAndOperationsList()
        getTransactionSigners()
        getSorobanBalanceChanges()
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
                                getSorobanBalanceChanges()
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
                    signersCountInfo = calculateSignersCount(it)
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

    fun getSorobanBalanceChanges() {
        if (sorobanBalanceChanges == null && transactionItem.transaction.isSorobanTransaction()) {
            unsubscribeOnDestroy(
                interactor.getSorobanBalanceChanges(
                    transactionItem.transaction.sourceAccount,
                    transactionItem.transaction.envelopXdr
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        sorobanBalanceChanges = it
                        checkTransactionInfo()
                    },{
                        when (it) {
                            is NoInternetConnectionException -> {
                                viewState.showMessage(it.details)
                                handleNoInternetConnection()
                            }
                        }
                    })
            )
        }
    }

    /**
     * @return Triple - Signers count info container (Count To Confirm, Signed Count, is Vault Account Pending) or null.
     */
    private fun calculateSignersCount(accountResult: AccountResult): Triple<Int, Int, Boolean>? {
        var signersCountInfo: Triple<Int, Int, Boolean>? = null
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
                    val signedCount = accountResult.signers.filter { it.signed == true }.size
                    val isVaultAccountPending = accountResult.signers.any { it.isVaultAccount == true && it.signed == false }
                    signersCountInfo = Triple(countToConfirm, signedCount, isVaultAccountPending)

                    countSummaryStr = "$signedCount ${AppUtil.getString(R.string.of_label)} $countToConfirm"

                    // Show signatures count to submit additional info only for non AUTH_CHALLENGE transactions.
                    if (transactionItem.transaction.transactionType != AUTH_CHALLENGE) {
                        countToSubmitStr = AppUtil.getQuantityString(
                            R.plurals.transaction_details_signatures_count_to_submit_label,
                            countToConfirm,
                            countToConfirm
                        )
                    }
                }
            }
        }

        viewState.showSignersCount(countSummaryStr, countToSubmitStr)

        return signersCountInfo
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
        // Handle transaction's status.
        val isTrValid by lazy { transactionItem.sequenceOutdatedAt.isNullOrEmpty() }
        when (transactionItem.status) {
            PENDING -> {
                // Check transaction's submitted state.
                if (transactionItem.submittedAt.isNullOrEmpty()) {
                    if (!isTrValid) {
                        viewState.showWarningLabel(
                            AppUtil.getString(R.string.transaction_details_invalid_sequence_number_description),
                            R.color.color_d9534f
                        )
                    }
                    viewState.setActionBtnState(
                        isConfirmVisible = true,
                        isDeclineVisible = true,
                        isConfirmEnabled = isTrValid
                    )
                } else {
                    viewState.setActionBtnState(
                        isConfirmVisible = false,
                        isDeclineVisible = false
                    )
                }
            }

            IMPORT_XDR -> {
                if (!isTrValid) {
                    viewState.showWarningLabel(
                        AppUtil.getString(R.string.transaction_details_invalid_sequence_number_description),
                        R.color.color_d9534f
                    )
                }
                viewState.setActionBtnState(
                    isConfirmVisible = true,
                    isDeclineVisible = true,
                    isConfirmEnabled = isTrValid
                )
            }

            SIGNED -> {
                viewState.updateMenuItemsVisibility(setOf(R.id.action_copy_signed_xdr))
                viewState.showWarningLabel(
                    AppUtil.getString(
                        if (!isTrValid) R.string.transaction_details_invalid_sequence_number_description else R.string.transaction_details_decline_signed_transaction_description
                    ),
                    if (!isTrValid) R.color.color_d9534f else R.color.color_9b9b9b
                )
                viewState.setActionBtnState(
                    isConfirmVisible = false,
                    isDeclineVisible = true
                )
            }

            CANCELLED -> viewState.setActionBtnState(
                isConfirmVisible = false,
                isDeclineVisible = false
            )
        }

        // Prepare operations list or show single operation:
        // when transaction has only one operation - show operation details screen immediately, else - list.
        if (transactionItem.transaction.operations.size > 1) {
            viewState.initOperationList(
                when (transactionItem.transaction.transactionType) {
                    AUTH_CHALLENGE -> R.string.transaction_details_challenge_title
                    else -> R.string.toolbar_transaction_details_title
                },
                mutableListOf<Int>().apply {
                    // Prepare operations list for show it.
                    for (operation in transactionItem.transaction.operations) {
                        val resId: Int =
                            TsUtil.getTransactionOperationName(operation)
                        if (resId != UNDEFINED_VALUE) {
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

    fun operationDetailsClicked(opPosition: Int,scrollX: Int, scrollY: Int) {
        prepareOperationDetails(opPosition, isInitState = false)

        // Save scroll X, Y positions and scroll details to the top.
        initialScrollX = scrollX
        initialScrollY = scrollY
        viewState.scrollTo(0, 0)
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
                    R.string.transaction_details_challenge_title // Specific case for the 'Single Operation' Challenge Transaction.
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
                val minFee = transactionItem.transaction.let {
                    val min = BigDecimal(BASE_FEE).multiply(BigDecimal(it.operations.size))
                    val resourceFee = if (it.isSorobanTransaction()) {
                        try {
                            BigDecimal(it.sorobanData?.resourceFee ?: "0").multiply(BigDecimal(STROOP))
                        } catch (exc: NumberFormatException) {
                            BigDecimal.ZERO
                        }
                    } else {
                        BigDecimal.ZERO
                    }

                    min.plus(resourceFee)
                }
                val fee = transactionItem.transaction.fee
                val addedAt = transactionItem.addedAt

                if (!memo.value.isNullOrEmpty()) {
                    val memoTitle = TsUtil.getMemoTypeStr(AppUtil.getAppContext(), memo).run {
                        if (isEmpty()) AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.transaction_memo) else "${AppUtil.getString(
                            com.lobstr.stellar.tsmapper.R.string.transaction_memo)} ($this)"
                    }

                    fields.add(OperationField(memoTitle, memo.value))
                }

                if (sourceAccount.isNotEmpty()) {
                    fields.add(OperationField(AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.transaction_source_account),
                        interactor.getAccountNames()[sourceAccount]?.plus(" (${AppUtil.ellipsizeStrInMiddle(sourceAccount, PK_TRUNCATE_COUNT_SHORT)})") ?: sourceAccount, sourceAccount))
                }

                if (minFee.compareTo(BigDecimal.ZERO) != 0) {
                    fields.add(OperationField(AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.transaction_min_fee),
                        "${TsUtil.getAmountRepresentationFromStr(minFee.stripTrailingZeros().toPlainString())} ${XLM.CODE}"
                    ))
                }

                if (fee != 0L) {
                    fields.add(OperationField(AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.transaction_fee), fee.let {
                        "${TsUtil.getAmountRepresentationFromStr(
                            BigDecimal(it).multiply(BigDecimal(STROOP)).stripTrailingZeros().toPlainString()
                        )} ${XLM.CODE}"
                    }))
                }

                if (!addedAt.isNullOrEmpty()) {
                    fields.add(OperationField(AppUtil.getString(R.string.transaction_details_added_at_label), AppUtil.formatDate(
                        ZonedDateTime.parse(addedAt).toInstant().toEpochMilli(),
                        if (DateFormat.is24HourFormat(AppUtil.getAppContext())) "MMM dd yyyy HH:mm" else "MMM dd yyyy h:mm a"
                    )))
                }

                createSorobanBalanceFields(fields)

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

    private fun createSorobanBalanceFields(fields: MutableList<OperationField>): MutableList<OperationField> {
        sorobanBalanceChanges?.forEach {
            Operation.mapAssetFields(
                AppUtil.getAppContext(),
                fields,
                it.asset
            )

            val amount = it.afterAmount.toBigDecimal().subtract(it.beforeAmount.toBigDecimal())
            val amountSrt = TsUtil.getAmountRepresentationFromStr(amount.stripTrailingZeros().toPlainString())

            val assetCode = when (it.asset) {
                is Asset.CanonicalAsset -> it.asset.assetCode
                is Asset.TrustLineAsset -> it.asset.asset?.assetCode
                is Asset.ChangeTrustAsset -> it.asset.asset?.assetCode
            }

            fields.add(
                OperationField(
                    AppUtil.getString(com.lobstr.stellar.tsmapper.R.string.op_field_balance_change),
                    "${if (amount.compareTo(BigDecimal.ZERO) >= 0) "+" else ""}$amountSrt${
                        if (!assetCode.isNullOrEmpty()) " ${
                            AppUtil.ellipsizeEndStr(
                                assetCode,
                                6
                            )
                        }" else ""
                    }"
                )
            )
        }
        return fields
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
        viewState.showWebPage(composeViewXdrUrl(transactionItem.transaction.envelopXdr))
    }

    fun btnConfirmClicked() {
        retrieveCountSequenceNumber()
    }

    /**
     * @param isSequenceWarningState Indicate state after receiving
     * 'Sequence Number' warning. True - don't show any other warnings.
     */
    private fun proceedConfirmAction(isSequenceWarningState: Boolean) {
        when {
            interactor.hasMnemonics() -> {
                when {
                    !isSequenceWarningState && interactor.isTrConfirmationEnabled() -> {
                        viewState.showProgressDialog(false)
                        viewState.showConfirmTransactionDialog(true, getConfirmationText())
                    }
                    else -> confirmTransaction()
                }
            }
            interactor.hasTangem() -> {
                // Change state for Tangem action - confirm.
                tangemSignTransactionState = false
                getTransactionInfo()
            }
        }
    }

    private fun getConfirmationText(): String = AppUtil.getString(signersCountInfo?.let { info ->
        val isNotAuthChallenge = transactionItem.transaction.transactionType != AUTH_CHALLENGE
        val isVaultAccountPending = info.third
        when {
            isNotAuthChallenge && isVaultAccountPending -> {
                val countToConfirm = info.first
                val signedCount = info.second
                val leftToSign = countToConfirm - signedCount

                when {
                    leftToSign == 1 -> R.string.transaction_confirmation_no_other_signatures_required_description
                    leftToSign > 1 -> R.string.transaction_confirmation_other_signatures_required_description
                    else -> R.string.transaction_confirmation_confirm_description
                }
            }
            else -> R.string.transaction_confirmation_confirm_description
        }
    } ?: R.string.transaction_confirmation_confirm_description)

    private fun getTransactionInfo() {
        if (confirmationInProcess) {
            return
        }

        unsubscribeOnDestroy(
            interactor.retrieveActualTransaction(transactionItem)
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    transactionItem = it
                    when (transactionItem.status) {
                        CANCELLED, SIGNED -> throw DefaultException(AppUtil.getString(R.string.transaction_details_msg_already_signed_or_declined))
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

    /**
     * Retrieve the number of transactions for the specified sequence
     * and show the alert when a user has multiple transactions with the same sequence number in the list.
     */
    private fun retrieveCountSequenceNumber() {
        unsubscribeOnDestroy(
            interactor.getCountSequenceNumber(transactionItem.transaction.sourceAccount, transactionItem.transaction.sequenceNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showProgressDialog(true)
                }
                .subscribe({ count ->
                    // When the Transactions Count for the specified sequence number grater than 1 (or 0 for the IMPORT_XDR flow) - show warning dialog. Else - proceed default behavior.
                    if (count > if (transactionItem.status == IMPORT_XDR) 0 else 1) {
                        viewState.showProgressDialog(false)
                        viewState.showSequenceNumberWarningDialog(true)
                    } else {
                        proceedConfirmAction(false)
                    }
                }, {
                    when (it) {
                        // Proceed default behavior for 404.
                        is HttpNotFoundException -> {
                            proceedConfirmAction(false)
                            return@subscribe
                        }
                        is UserNotAuthorizedException -> {
                            when (it.action) {
                                UserNotAuthorizedException.Action.AUTH_REQUIRED -> eventProviderModule.authEventSubject.onNext(
                                    Auth()
                                )
                                else -> {
                                    retrieveCountSequenceNumber()
                                    return@subscribe
                                }
                            }
                        }
                        is DefaultException -> {
                            viewState.showMessage(it.details)
                        }
                        else -> {
                            viewState.showMessage(it.message ?: "")
                        }
                    }
                    viewState.showProgressDialog(false)
                })
        )
    }

    fun btnDeclineClicked() {
        when {
            interactor.hasMnemonics() -> {
                when {
                    interactor.isTrConfirmationEnabled() -> viewState.showDeclineTransactionDialog()
                    else -> declineTransaction()
                }
            }
            interactor.hasTangem() -> {
                declineTransaction()
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

        unsubscribeOnDestroy(
            interactor.retrieveActualTransaction(
                transactionItem,
                !signedTransaction.isNullOrEmpty()
            )
                .subscribeOn(Schedulers.io())
                .flatMap {
                    transactionItem = it

                    when (transactionItem.status) {
                        CANCELLED, SIGNED -> throw DefaultException(AppUtil.getString(R.string.transaction_details_msg_already_signed_or_declined))
                    }

                    if (signedTransaction.isNullOrEmpty()) {
                        // Case for transaction received via Mnemonics.
                        interactor.signTransaction(it.transaction.envelopXdr)
                    } else {
                        // Case for transaction received via Tangem.
                        interactor.createTransaction(signedTransaction)
                    }
                }
                .flatMap { transaction ->
                    when (transactionItem.transaction.transactionType) {
                        AUTH_CHALLENGE -> {
                            needAdditionalSignatures = null
                            Single.fromCallable { transaction.toEnvelopeXdrBase64() }
                        }
                        else -> {
                            needAdditionalSignatures = false
                            interactor.confirmTransactionOnHorizon(transaction)
                                .flatMap { submitTransactionResponse ->
                                    when (submitTransactionResponse.tsResult.txResultCode.code) {
                                        TX_SUCCESS, TX_FEE_BUMP_INNER_SUCCESS -> {
                                            // Success status. Proceed default behavior.
                                        }
                                        TX_BAD_AUTH -> needAdditionalSignatures = true
                                        TX_FAILED -> {
                                            // One of the operations failed (none were applied).
                                            // Check op_bad_auth or try to take the first readable error.
                                            val opResultCodes = submitTransactionResponse.tsResult.opResultCodes

                                            val opErrors = opResultCodes.filter { resCode ->
                                                (resCode.code != OP_SUCCESS && resCode.code != OP_INNER)
                                            }

                                            val isOpBadAuth = opErrors.isNotEmpty() && opErrors.all { resCode ->
                                                resCode.code == OP_BAD_AUTH
                                            }

                                            if (isOpBadAuth) {
                                                needAdditionalSignatures = true
                                            } else {
                                                val firstOpResultCode = opErrors.firstOrNull { resCode ->
                                                    resCode.code != OP_BAD_AUTH
                                                }
                                                throw HorizonException(
                                                    details = firstOpResultCode?.message ?: submitTransactionResponse.tsResult.txResultCode.message,
                                                    shortDetails = firstOpResultCode?.let { code -> AppUtil.createOpResultShortDescription(code, opResultCodes.indexOf(code) + 1, opResultCodes.size) },
                                                    xdr = transaction.toEnvelopeXdrBase64()
                                                )
                                            }
                                        }
                                        else -> {
                                            // Error.
                                            throw HorizonException(
                                                details = submitTransactionResponse.tsResult.txResultCode.message,
                                                xdr = transaction.toEnvelopeXdrBase64()
                                            )
                                        }
                                    }

                                    Single.fromCallable { transaction.toEnvelopeXdrBase64() }
                                }
                        }
                    }
                }
                .flatMap { signedXdr ->
                    interactor.confirmTransactionOnServer(
                        needAdditionalSignatures ?: true,
                        transactionItem.status,
                        transactionItem.hash,
                        signedXdr
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
                        transactionItem.hash,
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
                            viewState.errorConfirmTransaction(Error(it.details, it.shortDetails, it.xdr))
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

    private fun declineTransaction() {
        // Check transaction status.
        when (transactionItem.status) {
            // IMPORT_XDR - 'success decline transaction' action without api call.
            IMPORT_XDR -> {
                viewState.successDeclineTransaction(transactionItem)

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
                            viewState.successDeclineTransaction(it)
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
                                        else -> declineTransaction()
                                    }
                                }
                                is InternalException -> viewState.showMessage(
                                    AppUtil.getString(
                                        R.string.api_error_internal_submit_transaction
                                    )
                                )
                                is HttpNotFoundException -> {
                                    viewState.showMessage(AppUtil.getString(R.string.transaction_details_msg_already_signed_or_declined))
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
            DECLINE_TRANSACTION -> declineTransaction()
            CONFIRM_TRANSACTION -> {
                viewState.showConfirmTransactionDialog(false)
                confirmTransaction()
            }
            SEQUENCE_NUMBER_WARNING -> {
                viewState.showSequenceNumberWarningDialog(false)
                proceedConfirmAction(true)
            }
        }
    }

    fun onAlertDialogNegativeButtonClicked(tag: String?) {
        when (tag) {
            CONFIRM_TRANSACTION -> viewState.showConfirmTransactionDialog(false)
            SEQUENCE_NUMBER_WARNING -> viewState.showSequenceNumberWarningDialog(false)
        }
    }

    fun onAlertDialogCanceled(tag: String?) {
        when (tag) {
            CONFIRM_TRANSACTION -> viewState.showConfirmTransactionDialog(false)
            SEQUENCE_NUMBER_WARNING -> viewState.showSequenceNumberWarningDialog(false)
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

        // Scroll operation's list to the saved position.
        if (backStackEntryCount == 1) {
            // Restore scroll fot the initial state
            viewState.scrollTo(initialScrollX, initialScrollY)
            // Reset initial scroll state.
            initialScrollX = 0
            initialScrollY = 0
        }
    }

    /**
     * @param key Reserved for future implementations.
     * @param value Reserved for future implementations.
     * @param tag Additional info for field (e.g. Asset for asset code)
     */
    fun additionalInfoValueClicked(key: String, value: String?, tag: Any?) {
        tag?.also {
            when {
                AppUtil.isValidAccount(it as? String) -> viewState.showEditAccountDialog(it as String)
                it is Asset.CanonicalAsset -> viewState.showAssetInfoDialog(it.assetCode, it.assetIssuer)
            }
        }
    }
}