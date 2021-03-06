package com.lobstr.stellar.vault.presentation.home.account_name

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentAccountNameBinding
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AccountNameDialogFragment : AlertDialogFragment(), AccountNameView, View.OnClickListener,
    TextView.OnEditorActionListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = AccountNameDialogFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentAccountNameBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<AccountNamePresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            publicKey = arguments?.getBundle(ARGUMENT_DIALOG_SPECIFIC_DATA)
                ?.getString(Constant.Bundle.BUNDLE_PUBLIC_KEY)!!
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getContentView(): Int {
        return R.layout.fragment_account_name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountNameBinding.bind(view)

        setListeners()
    }

    private fun setListeners() {
        binding.btnSave.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.edtAccountName.setOnEditorActionListener(this)
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
            binding.btnSave.id -> {
                AppUtil.closeKeyboard(activity)
                mPresenter.saveClicked(binding.edtAccountName.text.toString().trim())
            }
            binding.btnCancel.id -> mPresenter.cancelClicked()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (v?.id) {
            binding.edtAccountName.id -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mPresenter.saveClicked(binding.edtAccountName.text.toString().trim())
                }
            }
        }
        return false
    }

    override fun setupTitle(title: String?) {
        binding.tvTitle.text = title
    }

    override fun setAccountName(accountName: String?) {
        binding.edtAccountName.setText(accountName)
    }

    override fun closeScreen() {
        dismiss()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}