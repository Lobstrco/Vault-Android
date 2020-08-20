package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_edit_account.*
import moxy.ktx.moxyPresenter


class EditAccountDialogFragment : BaseBottomSheetDialog(), EditAccountView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = EditAccountDialogFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { EditAccountPresenter(
        arguments?.getString(Constant.Bundle.BUNDLE_PUBLIC_KEY)!!
    )}

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = if (mView == null) inflater.inflate(R.layout.fragment_edit_account, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnCopyPublicKey.setOnClickListener(this)
        btnOpenExplorer.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            btnCopyPublicKey.id -> mPresenter.copyPublicKeyClicked()
            btnOpenExplorer.id -> mPresenter.openExplorerClicked()
        }
    }

    override fun copyToClipBoard(text: String) {
        dismiss()
        AppUtil.copyToClipboard(context, text)
    }

    override fun openExplorer(url: String) {
        dismiss()
        AppUtil.openWebPage(context, url)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}