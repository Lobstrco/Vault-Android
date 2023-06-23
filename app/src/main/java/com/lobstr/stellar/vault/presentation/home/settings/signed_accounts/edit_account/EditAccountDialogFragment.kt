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
            publicKey = requireArguments().getString(Constant.Bundle.BUNDLE_PUBLIC_KEY)!!
            manageAccountName =
                requireArguments().getBoolean(Constant.Bundle.BUNDLE_MANAGE_ACCOUNT_NAME, false)
            showNetworkExplorer =
                requireArguments().getBoolean(Constant.Bundle.BUNDLE_SHOW_NETWORK_EXPLORER, true)
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
        binding.apply {
            btnCopyPublicKey.setSafeOnClickListener { mPresenter.copyPublicKeyClicked() }
            btnOpenExplorer.setSafeOnClickListener { mPresenter.openExplorerClicked() }
            btnSetNickName.setSafeOnClickListener { mPresenter.setNickNameClicked() }
            btnClearNickName.setSafeOnClickListener { mPresenter.clearNickNameClicked() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setAccountActionButton(text: String?) {
        binding.apply {
            btnSetNickName.text = text
            btnSetNickName.isVisible = !text.isNullOrEmpty()
        }
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