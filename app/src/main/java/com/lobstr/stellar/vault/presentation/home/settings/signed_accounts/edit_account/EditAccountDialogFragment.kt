package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobstr.stellar.vault.databinding.FragmentEditAccountBinding
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
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

    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnCopyPublicKey.setOnClickListener(this)
        binding.btnOpenExplorer.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnCopyPublicKey.id -> mPresenter.copyPublicKeyClicked()
            binding.btnOpenExplorer.id -> mPresenter.openExplorerClicked()
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