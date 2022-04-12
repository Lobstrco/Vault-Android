package com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lobstr.stellar.vault.databinding.FragmentEditAccountBinding
import com.lobstr.stellar.vault.presentation.BaseBottomSheetDialog
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class EditAccountDialogFragment : BaseBottomSheetDialog(), EditAccountView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = EditAccountDialogFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var presenterProvider: Provider<EditAccountPresenter>

    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            publicKey = arguments?.getString(Constant.Bundle.BUNDLE_PUBLIC_KEY)!!
            manageAccountName =
                arguments?.getBoolean(Constant.Bundle.BUNDLE_MANAGE_ACCOUNT_NAME) ?: false
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnCopyPublicKey.setSafeOnClickListener { mPresenter.copyPublicKeyClicked() }
        binding.btnOpenExplorer.setSafeOnClickListener { mPresenter.openExplorerClicked() }
        binding.btnSetNickName.setSafeOnClickListener { mPresenter.setNickNameClicked() }
        binding.btnClearNickName.setSafeOnClickListener { mPresenter.clearNickNameClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setAccountActionButton(text: String?) {
        binding.btnSetNickName.text = text
        binding.btnSetNickName.isVisible = !text.isNullOrEmpty()
    }

    override fun showClearAccountButton(show: Boolean) {
        binding.btnClearNickName.isVisible = show
    }

    override fun showSetNickNameFlow(publicKey: String) {
        // Notify target about success.
        ((parentFragment ?: activity) as? OnEditAccountDialogListener)?.onSetAccountNickNameClicked(
            publicKey
        )
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun openExplorer(url: String) {
        AppUtil.openWebPage(context, url)
    }

    override fun closeScreen() {
        dismiss()
    }

    override fun showNetworkExplorerButton(show: Boolean) {
        binding.btnOpenExplorer.isVisible = show
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    interface OnEditAccountDialogListener {
        fun onSetAccountNickNameClicked(publicKey: String)
    }
}