package com.lobstr.stellar.vault.presentation.home.account_name.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentAddAccountNameBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AddAccountNameFragment : BaseFragment(), AddAccountNameView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentAddAccountNameBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<AddAccountNamePresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get() }

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
        _binding = FragmentAddAccountNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            btnSave.setSafeOnClickListener {
                mPresenter.saveClicked(
                    edtAccountPublicKey.text.toString().trim(),
                    binding.edtAccountName.text.toString().trim()
                )
            }
            edtAccountPublicKey.doAfterTextChanged {
                mPresenter.addressChanged(
                    it?.trim()?.length ?: 0,
                    edtAccountName.text.toString().trim().length,
                )
            }
            edtAccountName.doAfterTextChanged {
                mPresenter.nameChanged(
                    it?.trim()?.length ?: 0,
                    edtAccountPublicKey.text.toString().trim().length
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setSaveEnabled(enabled: Boolean) {
        binding.btnSave.isEnabled = enabled
    }

    override fun showAddressFormError(show: Boolean) {
        binding.apply {
            edtAccountPublicKey.setBackgroundResource(if (show) R.drawable.shape_input_error_edit_text else R.drawable.shape_input_edit_text)
            tvAccountPublicKeyRestriction.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (show) R.color.color_d9534f else R.color.color_4a4a4a
                )
            )
        }
    }

    override fun showNameFormError(show: Boolean) {
        binding.apply {
            edtAccountName.setBackgroundResource(if (show) R.drawable.shape_input_error_edit_text else R.drawable.shape_input_edit_text)
            tvAccountNameRestriction.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (show) R.color.color_d9534f else R.color.color_4a4a4a
                )
            )
        }
    }

    override fun closeScreen() {
        requireActivity().finish()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}