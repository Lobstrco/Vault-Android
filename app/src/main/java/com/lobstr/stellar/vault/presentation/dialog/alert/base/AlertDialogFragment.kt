package com.lobstr.stellar.vault.presentation.dialog.alert.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatDialogFragment
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment.DialogIdentifier.PROGRESS
import com.lobstr.stellar.vault.presentation.dialog.alert.progress.ProgressDialog


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

    protected var mContentView: View? = null

    object DialogFragmentIdentifier {
        const val PROGRESS_DIALOG = "PROGRESS_DIALOG"
        const val FINGERPRINT_INFO_DIALOG = "FINGERPRINT_INFO_DIALOG"
        const val DENY_TRANSACTION = "DENY_TRANSACTION"
        const val IMPORT_XDR = "IMPORT_XDR"
        const val PUBLIC_KEY = "PUBLIC_KEY"
        const val DENY_ACCOUNT_CREATION = "DENY_ACCOUNT_CREATION"
        const val LOG_OUT = "LOG_OUT"
    }

    object DialogIdentifier {
        const val PROGRESS = 1
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

            .setNegativeButton(mNegativeBtnText, if (mOnBaseAlertDialogListener == null)
                null
            else
                DialogInterface.OnClickListener { dialogInterface, _ ->
                    this@AlertDialogFragment.onNegativeBtnClick(
                        dialogInterface
                    )
                })

            .setPositiveButton(mPositiveBtnText, if (mOnBaseAlertDialogListener == null)
                null
            else
                DialogInterface.OnClickListener { dialogInterface, _ ->
                    this@AlertDialogFragment.onPositiveBtnClick(
                        dialogInterface
                    )
                })

            .setOnCancelListener(if (mOnBaseAlertDialogListener == null)
                null
            else
                DialogInterface.OnCancelListener { dialogInterface ->
                    this@AlertDialogFragment.onCanceled(
                        dialogInterface
                    )
                })
            .create()
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

    protected fun getContentView(): Int {
        return mView
    }

    protected fun onPositiveBtnClick(dialogInterface: DialogInterface) {
        if (mOnBaseAlertDialogListener is OnDefaultAlertDialogListener) {
            (mOnBaseAlertDialogListener as OnDefaultAlertDialogListener)
                .onPositiveBtnClick(this@AlertDialogFragment.tag, dialogInterface)
        }
    }

    protected fun onNegativeBtnClick(dialogInterface: DialogInterface) {
        if (mOnBaseAlertDialogListener is OnDefaultAlertDialogListener) {
            (mOnBaseAlertDialogListener as OnDefaultAlertDialogListener)
                .onNegativeBtnClick(this@AlertDialogFragment.tag, dialogInterface)
        }
    }

    protected fun onCanceled(dialogInterface: DialogInterface) {
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

        fun onCancel(tag: String?, dialogInterface: DialogInterface)
    }

}