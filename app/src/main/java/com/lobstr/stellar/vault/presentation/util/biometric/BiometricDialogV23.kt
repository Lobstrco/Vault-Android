package com.lobstr.stellar.vault.presentation.util.biometric

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lobstr.stellar.vault.R

class BiometricDialogV23 : BottomSheetDialog, View.OnClickListener {

    private var mContext: Context? = null

    private var btnCancel: Button? = null
    private var imgLogo: ImageView? = null
    private var itemTitle: TextView? = null
    private var itemDescription: TextView? = null
    private var itemSubtitle: TextView? = null
    private var itemStatus: TextView? = null

    private var biometricCallback: BiometricCallback? = null

    constructor(context: Context) : super(context, R.style.BottomSheetDialogTheme) {
        this.mContext = context.applicationContext
        setDialogView()
    }

    constructor(context: Context, biometricCallback: BiometricCallback) : super(
        context,
        R.style.BottomSheetDialogTheme
    ) {
        this.mContext = context.applicationContext
        this.biometricCallback = biometricCallback
        setDialogView()
    }

    constructor(context: Context, theme: Int) : super(context, theme)

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener)

    private fun setDialogView() {
        val bottomSheetView = layoutInflater.inflate(R.layout.layout_biometric_bottom_sheet, null)
        setContentView(bottomSheetView)

        btnCancel = findViewById(R.id.btn_cancel)
        btnCancel!!.setOnClickListener(this)

        imgLogo = findViewById(R.id.img_logo)
        itemTitle = findViewById(R.id.item_title)
        itemStatus = findViewById(R.id.item_status)
        itemSubtitle = findViewById(R.id.item_subtitle)
        itemDescription = findViewById(R.id.item_description)

        updateLogo()
    }

    fun setTitle(title: String) {
        itemTitle!!.text = title
    }

    fun updateStatus(status: String) {
        itemStatus!!.text = status
    }

    fun setSubtitle(subtitle: String) {
        itemSubtitle!!.text = subtitle
    }

    fun setDescription(description: String) {
        itemDescription!!.text = description
    }

    fun setButtonText(negativeButtonText: String) {
        btnCancel!!.text = negativeButtonText
    }

    private fun updateLogo() {
        try {
            val drawable = context.packageManager.getApplicationIcon(context.packageName)
            imgLogo!!.setImageDrawable(drawable)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View) {
        dismiss()
        biometricCallback?.onAuthenticationCancelled()
    }
}
