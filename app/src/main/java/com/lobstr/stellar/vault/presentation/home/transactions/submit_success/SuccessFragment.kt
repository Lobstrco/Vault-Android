package com.lobstr.stellar.vault.presentation.home.transactions.submit_success


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobstr.stellar.vault.databinding.FragmentSuccessBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SuccessFragment : BaseFragment(), SuccessView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SuccessFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<SuccessPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get().apply {
        envelopeXdr = requireArguments().getString(Constant.Bundle.BUNDLE_ENVELOPE_XDR)!!
        status = requireArguments().getByte(Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS, SUCCESS)
    } }

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
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnCopyXdr.setOnClickListener(this)
        binding.btnDone.setOnClickListener(this)
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
            binding.btnCopyXdr.id -> mPresenter.copyXdrClicked()
            binding.btnDone.id -> mPresenter.doneClicked()
        }
    }

    override fun vibrate(pattern: LongArray) {
        AppUtil.vibrate(requireContext(), pattern)
    }

    override fun setupXdr(xdr: String) {
        binding.tvXdr.text = xdr
    }

    override fun finishScreen() {
        activity?.onBackPressed()
    }

    override fun setAdditionalSignaturesInfoEnabled(enabled: Boolean) {
        binding.tvAdditionalSignaturesDescription.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.llXdrContainer.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    override fun showXdrContainer(show: Boolean) {
        binding.llXdrContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
