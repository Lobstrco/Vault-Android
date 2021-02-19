package com.lobstr.stellar.vault.presentation.dialog.alert.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatDialogFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.APP_UPDATE
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.PROGRESS
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.RATE_US
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.SUGGEST_RATE_US
import com.lobstr.stellar.vault.presentation.dialog.alert.progress.ProgressDialog
import com.lobstr.stellar.vault.presentation.home.app_update.AppUpdateDialogFragment
import com.lobstr.stellar.vault.presentation.home.rate_us.common.RateUsDialogFragment
import com.lobstr.stellar.vault.presentation.home.rate_us.suggest.SuggestRateUsDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil


open class AlertDialogFragment : BaseMvpAppCompatDialogFragment() {

    // ===========================================================
    // Fields
    // ===========================================================

    protected var mOnBaseAlertDialogListener: OnBaseAlertDialogListener? = null

    // Required parameters.
    protected var isCalledInFragment: Boolean = false

    // Optional parameters.
    private var cancelableOf: Boolean = false

    @LayoutRes
    private var mView: Int = 0

    private var mTitle: CharSequence? = null
    private var mMessage: CharSequence? = null

    private var mNegativeBtnText: CharSequence? = null
    private var mPositiveBtnText: CharSequence? = null
    private var mNeutralBtnText: CharSequence? = null
    private var negativeBtnEnabled: Boolean = true
    private var positiveBtnEnabled: Boolean = true
    private var neutralBtnEnabled: Boolean = true

    protected var mContentView: View? = null

    object DialogFragmentIdentifier {
        const val PROGRESS_DIALOG = "PROGRESS_DIALOG"
        const val BIOMETRIC_INFO_DIALOG = "BIOMETRIC_INFO_DIALOG"
        const val DENY_TRANSACTION = "DENY_TRANSACTION"
        const val CONFIRM_TRANSACTION = "CONFIRM_TRANSACTION"
        const val PUBLIC_KEY = "PUBLIC_KEY"
        const val DENY_ACCOUNT_CREATION = "DENY_ACCOUNT_CREATION"
        const val LOG_OUT = "LOG_OUT"
        const val EDIT_ACCOUNT = "EDIT_ACCOUNT"
        const val RATE_US = "RATE_US"
        const val SUGGEST_RATE_US = "SUGGEST_RATE_US"
        const val COMMON_PIN_PATTERN = "COMMON_PIN_PATTERN"
        const val CLEAR_TRANSACTIONS = "CLEAR_TRANSACTIONS"
        const val NFC_INFO_DIALOG = "NFC_INFO_DIALOG"
        const val TANGEM = "TANGEM"
        const val INFO = "INFO"
        const val APP_UPDATE = "APP_UPDATE"
    }

    object DialogIdentifier {
        const val PROGRESS = 1
        const val SUGGEST_RATE_US = 2
        const val RATE_US = 3
        const val APP_UPDATE = 4
    }

    companion object {

        // ===========================================================
        // Constants
        // ===========================================================

        private val LOG_TAG = AlertDialogFragment::class.java.simpleName

        const val ARGUMENT_DIALOG_IS_CALLED_IN_FRAGMENT = "ARGUMENT_DIALOG_IS_CALLED_IN_FRAGMENT"
        const val ARGUMENT_DIALOG_IS_CANCELABLE = "ARGUMENT_DIALOG_IS_CANCELABLE"
        const val ARGUMENT_DIALOG_TITLE = "ARGUMENT_DIALOG_TITLE"
        const val ARGUMENT_DIALOG_MESSAGE = "ARGUMENT_DIALOG_MESSAGE"
        const val ARGUMENT_DIALOG_VIEW = "ARGUMENT_DIALOG_VIEW"
        const val ARGUMENT_DIALOG_NEGATIVE_BTN_TEXT = "ARGUMENT_DIALOG_NEGATIVE_BTN_TEXT"
        const val ARGUMENT_DIALOG_POSITIVE_BTN_TEXT = "ARGUMENT_DIALOG_POSITIVE_BTN_TEXT"
        const val ARGUMENT_DIALOG_NEUTRAL_BTN_TEXT = "ARGUMENT_DIALOG_NEUTRAL_BTN_TEXT"
        const val ARGUMENT_DIALOG_NEGATIVE_BTN_ENABLED = "ARGUMENT_DIALOG_NEGATIVE_BTN_ENABLED"
        const val ARGUMENT_DIALOG_POSITIVE_BTN_ENABLED = "ARGUMENT_DIALOG_POSITIVE_BTN_ENABLED"
        const val ARGUMENT_DIALOG_NEUTRAL_BTN_ENABLED = "ARGUMENT_DIALOG_NEUTRAL_BTN_ENABLED"
        const val ARGUMENT_DIALOG_SPECIFIC_DATA = "ARGUMENT_DIALOG_SPECIFIC_DATA"

        // ===========================================================
        // Constructors
        // ===========================================================

        private fun factory(dialogId: Int, bundle: Bundle): AlertDialogFragment {

            val alertDialogFragment: AlertDialogFragment

            when (dialogId) {

                PROGRESS -> {
                    alertDialogFragment = ProgressDialog()
                    Log.i(LOG_TAG, " fabric: ProgressDialog")
                }

                SUGGEST_RATE_US -> {
                    alertDialogFragment =
                        SuggestRateUsDialogFragment()
                    Log.i(LOG_TAG, " fabric: SuggestRateUsDialogFragment")
                }

                RATE_US -> {
                    alertDialogFragment =
                        RateUsDialogFragment()
                    Log.i(LOG_TAG, " fabric: RateUsDialogFragment")
                }

                APP_UPDATE -> {
                    alertDialogFragment = AppUpdateDialogFragment()
                    Log.i(LOG_TAG, " fabric: AppUpdateDialogFragment")
                }

                else -> {
                    alertDialogFragment = AlertDialogFragment()
                    Log.i(LOG_TAG, " fabric: default AlertDialogFragment")
                }
            }

            alertDialogFragment.arguments = bundle

            return alertDialogFragment
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        receiveArguments()

        initView()

        isCancelable = cancelableOf

        return AlertDialog.Builder(requireActivity(), theme)
            .setTitle(mTitle)
            .setMessage(mMessage)
            .setView(mContentView)
            .setNegativeButton(mNegativeBtnText) { dialogInterface, _ ->
                this@AlertDialogFragment.onNegativeBtnClick(
                    dialogInterface
                )
            }
            .setPositiveButton(mPositiveBtnText) { dialogInterface, _ ->
                this@AlertDialogFragment.onPositiveBtnClick(
                    dialogInterface
                )
            }
            .setNeutralButton(mNeutralBtnText) { dialogInterface, _ ->
                this@AlertDialogFragment.onNeutralBtnClick(
                    dialogInterface
                )
            }
            .create()
    }

    override fun getTheme(): Int {
        return R.style.AlertDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mContentView
    }

    override fun onCancel(dialog: DialogInterface) {
        this@AlertDialogFragment.onCanceled(
            dialog
        )
    }

    /**
     * Used for usual dialog showing. Here checking only visibility status of the previous dialog.
     */
    override fun show(manager: FragmentManager, tag: String?) {
        // If dialog is showed - skip it.
        if ((manager.findFragmentByTag(tag) as? AlertDialogFragment)?.dialog != null) {
            return
        }

        super.show(manager, tag)
    }

    /**
     * Used for dialog showing with the strong check of previous dialog reference.
     * Use it when several identical dialogs called concurrently.
     */
    fun showInstant(manager: FragmentManager, tag: String?) {
        // If dialog don't equals null - skip it.
        if ((manager.findFragmentByTag(tag) as? AlertDialogFragment)?.dialog != null) {
            return
        }

        super.show(manager, tag)
    }

    override fun onStart() {
        super.onStart()
        // Enable or disable buttons.
        (dialog as? AlertDialog)?.getButton(Dialog.BUTTON_NEGATIVE)?.isEnabled = negativeBtnEnabled
        (dialog as? AlertDialog)?.getButton(Dialog.BUTTON_POSITIVE)?.isEnabled = positiveBtnEnabled
        (dialog as? AlertDialog)?.getButton(Dialog.BUTTON_NEUTRAL)?.isEnabled = neutralBtnEnabled
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Dismiss dialog for all specific cases.
        dismissAllowingStateLoss()
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private fun receiveArguments() {
        if (arguments == null) {
            return
        }

        isCalledInFragment = requireArguments().getBoolean(ARGUMENT_DIALOG_IS_CALLED_IN_FRAGMENT)
        cancelableOf = requireArguments().getBoolean(ARGUMENT_DIALOG_IS_CANCELABLE)

        mView = requireArguments().getInt(ARGUMENT_DIALOG_VIEW)
        mTitle = requireArguments().getCharSequence(ARGUMENT_DIALOG_TITLE)
        mMessage = requireArguments().getCharSequence(ARGUMENT_DIALOG_MESSAGE)
        mNegativeBtnText = requireArguments().getCharSequence(ARGUMENT_DIALOG_NEGATIVE_BTN_TEXT)
        mPositiveBtnText = requireArguments().getCharSequence(ARGUMENT_DIALOG_POSITIVE_BTN_TEXT)
        mNeutralBtnText = requireArguments().getCharSequence(ARGUMENT_DIALOG_NEUTRAL_BTN_TEXT)
        negativeBtnEnabled = requireArguments().getBoolean(ARGUMENT_DIALOG_NEGATIVE_BTN_ENABLED, true)
        positiveBtnEnabled = requireArguments().getBoolean(ARGUMENT_DIALOG_POSITIVE_BTN_ENABLED, true)
        neutralBtnEnabled = requireArguments().getBoolean(ARGUMENT_DIALOG_NEUTRAL_BTN_ENABLED, true)

        mOnBaseAlertDialogListener = if (isCalledInFragment)
            if (parentFragment is OnBaseAlertDialogListener) parentFragment as OnBaseAlertDialogListener? else null
        else if (activity is OnBaseAlertDialogListener) activity as OnBaseAlertDialogListener? else null
    }

    private fun initView() {
        mContentView = if (getContentView() == 0)
            null
        else
            requireActivity().layoutInflater.inflate(getContentView(), null)
    }

    protected open fun getContentView(): Int {
        return mView
    }

    protected open fun onPositiveBtnClick(dialogInterface: DialogInterface) {
        if (mOnBaseAlertDialogListener is OnDefaultAlertDialogListener) {
            (mOnBaseAlertDialogListener as OnDefaultAlertDialogListener)
                .onPositiveBtnClick(this@AlertDialogFragment.tag, dialogInterface)
        }
    }

    protected open fun onNegativeBtnClick(dialogInterface: DialogInterface) {
        if (mOnBaseAlertDialogListener is OnDefaultAlertDialogListener) {
            (mOnBaseAlertDialogListener as OnDefaultAlertDialogListener)
                .onNegativeBtnClick(this@AlertDialogFragment.tag, dialogInterface)
        }
    }

    protected open fun onNeutralBtnClick(dialogInterface: DialogInterface) {
        if (mOnBaseAlertDialogListener is OnDefaultAlertDialogListener) {
            (mOnBaseAlertDialogListener as OnDefaultAlertDialogListener)
                .onNeutralBtnClick(this@AlertDialogFragment.tag, dialogInterface)
        }
    }

    protected open fun onCanceled(dialogInterface: DialogInterface) {
        if (mOnBaseAlertDialogListener is OnDefaultAlertDialogListener) {
            (mOnBaseAlertDialogListener as OnDefaultAlertDialogListener)
                .onCancel(this@AlertDialogFragment.tag, dialogInterface)
        }
    }

    // Use these methods for enable and disable Alert Dialog buttons.

    protected fun setNegativeBtnEnabled(enabled: Boolean){
        negativeBtnEnabled = enabled
        (dialog as? AlertDialog)?.getButton(Dialog.BUTTON_NEGATIVE)?.isEnabled = enabled
    }

    protected fun setPositiveBtnEnabled(enabled: Boolean){
        positiveBtnEnabled = enabled
        (dialog as? AlertDialog)?.getButton(Dialog.BUTTON_POSITIVE)?.isEnabled = enabled
    }

    protected fun setNeutralBtnEnabled(enabled: Boolean){
        neutralBtnEnabled = enabled
        (dialog as? AlertDialog)?.getButton(Dialog.BUTTON_NEUTRAL)?.isEnabled = enabled
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    /**
     * Determine where dialog was called.
     *
     * @param isCalledInFragment True: in Fragment. False: in Activity.
     */

    class Builder(private val isCalledInFragment: Boolean) {

        // optional parameters
        private var cancelableOf: Boolean = false

        private var dialogId: Int = 0

        @LayoutRes
        private var view: Int = 0

        private var title: CharSequence? = null
        private var message: CharSequence? = null

        private var negativeBtnText: CharSequence? = null
        private var positiveBtnText: CharSequence? = null
        private var neutralBtnText: CharSequence? = null
        private var negativeBtnEnabled: Boolean = true
        private var positiveBtnEnabled: Boolean = true
        private var neutralBtnEnabled: Boolean = true

        private var dialogBundle: Bundle? = null

        fun setCancelable(isCancelable: Boolean): Builder {
            this.cancelableOf = isCancelable
            return this
        }

        fun setTitle(@StringRes title: Int): Builder {
            this.title = AppUtil.getString(title)
            return this
        }

        fun setTitle(title: CharSequence?): Builder {
            this.title = title
            return this
        }

        fun setMessage(@StringRes message: Int): Builder {
            this.message = AppUtil.getString(message)
            return this
        }

        fun setMessage(message: CharSequence?): Builder {
            this.message = message
            return this
        }

        /**
         * Determine custom layout for dialog.
         *
         * @param view Layout resource.
         * @return Builder.
         */
        fun setView(@LayoutRes view: Int): Builder {
            this.view = view
            return this
        }

        fun setNegativeBtnText(@StringRes negativeBtnText: Int): Builder {
            this.negativeBtnText = AppUtil.getString(negativeBtnText)
            return this
        }

        fun setNegativeBtnText(negativeBtnText: CharSequence?): Builder {
            this.negativeBtnText = negativeBtnText
            return this
        }

        fun setPositiveBtnText(@StringRes positiveBtnText: Int): Builder {
            this.positiveBtnText = AppUtil.getString(positiveBtnText)
            return this
        }

        fun setPositiveBtnText(positiveBtnText: CharSequence?): Builder {
            this.positiveBtnText = positiveBtnText
            return this
        }

        fun setNeutralBtnText(@StringRes neutralBtnText: Int): Builder {
            this.neutralBtnText = AppUtil.getString(neutralBtnText)
            return this
        }

        fun setNeutralBtnText(neutralBtnText: CharSequence?): Builder {
            this.neutralBtnText = neutralBtnText
            return this
        }

        fun setNegativeBtnEnabled(negativeBtnEnabled: Boolean): Builder {
            this.negativeBtnEnabled = negativeBtnEnabled
            return this
        }

        fun setPositiveBtnEnabled(positiveBtnEnabled: Boolean): Builder {
            this.positiveBtnEnabled = positiveBtnEnabled
            return this
        }

        fun setNeutralBtnEnabled(neutralBtnEnabled: Boolean): Builder {
            this.neutralBtnEnabled = neutralBtnEnabled
            return this
        }

        /**
         * Use it for specific AlertDialogFragments.
         *
         * @param dialogId Specific dialog id (used for dialog factory).
         * @param bundle Data passed to specific dialog. Retrieve it by const ARGUMENT_DIALOG_SPECIFIC_DATA.
         * @see ARGUMENT_DIALOG_SPECIFIC_DATA
         * @return Builder.
         */
        fun setSpecificDialog(dialogId: Int, bundle: Bundle? = null): Builder {
            this.dialogId = dialogId
            this.dialogBundle = bundle
            return this
        }

        fun create(): AlertDialogFragment {

            val bundle = Bundle()

            bundle.putBoolean(ARGUMENT_DIALOG_IS_CALLED_IN_FRAGMENT, isCalledInFragment)
            bundle.putBoolean(ARGUMENT_DIALOG_IS_CANCELABLE, cancelableOf)

            bundle.putInt(ARGUMENT_DIALOG_VIEW, view)

            bundle.putCharSequence(ARGUMENT_DIALOG_TITLE, title)
            bundle.putCharSequence(ARGUMENT_DIALOG_MESSAGE, message)
            bundle.putCharSequence(ARGUMENT_DIALOG_NEGATIVE_BTN_TEXT, negativeBtnText)
            bundle.putCharSequence(ARGUMENT_DIALOG_POSITIVE_BTN_TEXT, positiveBtnText)
            bundle.putCharSequence(ARGUMENT_DIALOG_NEUTRAL_BTN_TEXT, neutralBtnText)
            bundle.putBoolean(ARGUMENT_DIALOG_NEGATIVE_BTN_ENABLED, negativeBtnEnabled)
            bundle.putBoolean(ARGUMENT_DIALOG_POSITIVE_BTN_ENABLED, positiveBtnEnabled)
            bundle.putBoolean(ARGUMENT_DIALOG_NEUTRAL_BTN_ENABLED, neutralBtnEnabled)

            bundle.putBundle(ARGUMENT_DIALOG_SPECIFIC_DATA, dialogBundle)

            return factory(dialogId, bundle)
        }
    }

    /**
     * Extend your custom AlertDialogFragment action listener
     * from OnBaseActionListener.
     */
    interface OnBaseAlertDialogListener// Implement your methods if needed.

    interface OnDefaultAlertDialogListener : OnBaseAlertDialogListener {
        fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface)

        fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface)

        fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface)

        fun onCancel(tag: String?, dialogInterface: DialogInterface)
    }

}