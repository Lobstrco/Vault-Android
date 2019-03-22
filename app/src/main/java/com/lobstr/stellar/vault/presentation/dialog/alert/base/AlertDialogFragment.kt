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
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.PROGRESS
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.RATE_US
import com.lobstr.stellar.vault.presentation.dialog.alert.progress.ProgressDialog
import com.lobstr.stellar.vault.presentation.home.rate_us.RateUsDialogFragment


open class AlertDialogFragment : BaseMvpAppCompatDialogFragment() {

    // ===========================================================
    // Fields
    // ===========================================================

    protected var mOnBaseAlertDialogListener: OnBaseAlertDialogListener? = null

    // required parameters
    protected var isCalledInFragment: Boolean = false

    // optional parameters
    private var cancelableOf: Boolean = false

    @LayoutRes
    private var mView: Int = 0

    private var mTitle: CharSequence? = null
    private var mMessage: CharSequence? = null

    private var mNegativeBtnText: CharSequence? = null
    private var mPositiveBtnText: CharSequence? = null
    private var mNeutralBtnText: CharSequence? = null

    protected var mContentView: View? = null

    object DialogFragmentIdentifier {
        const val PROGRESS_DIALOG = "PROGRESS_DIALOG"
        const val FINGERPRINT_INFO_DIALOG = "FINGERPRINT_INFO_DIALOG"
        const val DENY_TRANSACTION = "DENY_TRANSACTION"
        const val CONFIRM_TRANSACTION = "CONFIRM_TRANSACTION"
        const val IMPORT_XDR = "IMPORT_XDR"
        const val PUBLIC_KEY = "PUBLIC_KEY"
        const val DENY_ACCOUNT_CREATION = "DENY_ACCOUNT_CREATION"
        const val LOG_OUT = "LOG_OUT"
        const val EDIT_ACCOUNT = "EDIT_ACCOUNT"
        const val RATE_US = "RATE_US"
        const val COMMON_PIN_PATTERN = "COMMON_PIN_PATTERN"
    }

    object DialogIdentifier {
        const val PROGRESS = 1
        const val RATE_US = 2
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

                RATE_US -> {
                    alertDialogFragment = RateUsDialogFragment()
                    Log.i(LOG_TAG, " fabric: RateUsDialogFragment")
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

        return AlertDialog.Builder(activity!!, R.style.AlertDialog)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mContentView
    }

    override fun onCancel(dialog: DialogInterface?) {
        this@AlertDialogFragment.onCanceled(
            dialog!!
        )
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        // if dialog is showed - skip it
        if ((manager?.findFragmentByTag(tag) as? AlertDialogFragment)?.dialog?.isShowing == true) {
            return
        }

        super.show(manager, tag)
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

        isCalledInFragment = arguments!!.getBoolean(ARGUMENT_DIALOG_IS_CALLED_IN_FRAGMENT)
        cancelableOf = arguments!!.getBoolean(ARGUMENT_DIALOG_IS_CANCELABLE)
        mView = arguments!!.getInt(ARGUMENT_DIALOG_VIEW)
        mTitle = arguments!!.getCharSequence(ARGUMENT_DIALOG_TITLE)
        mMessage = arguments!!.getCharSequence(ARGUMENT_DIALOG_MESSAGE)
        mNegativeBtnText = arguments!!.getCharSequence(ARGUMENT_DIALOG_NEGATIVE_BTN_TEXT)
        mPositiveBtnText = arguments!!.getCharSequence(ARGUMENT_DIALOG_POSITIVE_BTN_TEXT)
        mNeutralBtnText = arguments!!.getCharSequence(ARGUMENT_DIALOG_NEUTRAL_BTN_TEXT)

        mOnBaseAlertDialogListener = if (isCalledInFragment)
            if (parentFragment is OnBaseAlertDialogListener) parentFragment as OnBaseAlertDialogListener? else null
        else if (activity is OnBaseAlertDialogListener) activity as OnBaseAlertDialogListener? else null
    }

    private fun initView() {
        mContentView = if (getContentView() == 0)
            null
        else
            activity!!.layoutInflater.inflate(getContentView(), null)
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

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    /**
     * Determine where dialog was called
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

        private var dialogBundle: Bundle? = null

        fun setCancelable(isCancelable: Boolean): Builder {
            this.cancelableOf = isCancelable
            return this
        }

        fun setTitle(@StringRes title: Int): Builder {
            this.title = LVApplication.sAppComponent.context.getText(title)
            return this
        }

        fun setTitle(title: CharSequence?): Builder {
            this.title = title
            return this
        }

        fun setMessage(@StringRes message: Int): Builder {
            this.message = LVApplication.sAppComponent.context.getText(message)
            return this
        }

        fun setMessage(message: CharSequence?): Builder {
            this.message = message
            return this
        }

        /**
         * Determine custom layout for dialog
         *
         * @param view layout resource
         * @return Builder
         */
        fun setView(@LayoutRes view: Int): Builder {
            this.view = view
            return this
        }

        fun setNegativeBtnText(@StringRes negativeBtnText: Int): Builder {
            this.negativeBtnText = LVApplication.sAppComponent.context.getText(negativeBtnText)
            return this
        }

        fun setNegativeBtnText(negativeBtnText: CharSequence?): Builder {
            this.negativeBtnText = negativeBtnText
            return this
        }

        fun setPositiveBtnText(@StringRes positiveBtnText: Int): Builder {
            this.positiveBtnText = LVApplication.sAppComponent.context.getText(positiveBtnText)
            return this
        }

        fun setPositiveBtnText(positiveBtnText: CharSequence?): Builder {
            this.positiveBtnText = positiveBtnText
            return this
        }

        fun setNeutralBtnText(@StringRes neutralBtnText: Int): Builder {
            this.neutralBtnText = LVApplication.sAppComponent.context.getText(neutralBtnText)
            return this
        }

        fun setNeutralBtnText(neutralBtnText: CharSequence?): Builder {
            this.neutralBtnText = neutralBtnText
            return this
        }

        /**
         * Use it for specific AlertDialogFragments
         *
         * @param dialogId specific dialog id (used for dialog factory)
         * @param bundle   data passed to specific dialog
         * @return Builder
         */
        fun setSpecificDialog(dialogId: Int, bundle: Bundle?): Builder {
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

            bundle.putBundle(ARGUMENT_DIALOG_SPECIFIC_DATA, dialogBundle)

            return factory(dialogId, bundle)
        }
    }

    /**
     * you must extend your custom AlertDialogFragment action listener
     * from OnBaseActionListener
     */
    interface OnBaseAlertDialogListener// implement your methods if needed

    interface OnDefaultAlertDialogListener : OnBaseAlertDialogListener {
        fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface)

        fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface)

        fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface)

        fun onCancel(tag: String?, dialogInterface: DialogInterface)
    }

}