package com.lobstr.stellar.vault.presentation.home.transactions.submit_success


import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentSuccessBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import com.lobstr.stellar.vault.presentation.util.VibratorUtil
import com.lobstr.stellar.vault.presentation.util.VibratorUtil.VibrateType
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class SuccessFragment : BaseFragment(), SuccessView {

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
        addMenuProvider()
        setListeners()
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.success, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_info -> mPresenter.infoClicked()
                    R.id.action_view_transaction_details -> mPresenter.viewTransactionDetailsClicked()
                    R.id.action_copy_signed_xdr -> mPresenter.copySignedXdrClicked()
                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setListeners() {
        binding.apply {
            copyXdr.btnCopyXdr.setSafeOnClickListener { mPresenter.copyXdrClicked() }
            btnDone.setSafeOnClickListener { mPresenter.doneClicked() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun vibrate(type: VibrateType) {
        VibratorUtil.vibrate(requireContext(), type)
    }

    override fun setupXdr(xdr: String) {
        binding.copyXdr.tvXdr.text = xdr
    }

    override fun finishScreen() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun setAdditionalSignaturesInfoEnabled(enabled: Boolean) {
        binding.apply {
            tvAdditionalSignaturesDescription.isVisible = enabled
            copyXdr.root.isVisible = enabled
        }
    }

    override fun showXdrContainer(show: Boolean) {
        binding.copyXdr.root.isVisible = show
    }

    override fun showHelpScreen() {
        SupportManager.showFreshdeskHelpCenter(requireContext())
    }

    override fun showWebPage(url: String) {
        AppUtil.openWebPage(context, url)
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
