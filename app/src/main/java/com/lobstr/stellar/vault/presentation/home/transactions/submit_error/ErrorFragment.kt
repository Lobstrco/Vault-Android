package com.lobstr.stellar.vault.presentation.home.transactions.submit_error


import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentErrorBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ErrorFragment : BaseFragment(), ErrorView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ErrorFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<ErrorPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            error = requireArguments().getString(Constant.Bundle.BUNDLE_ERROR_MESSAGE)!!
            envelopeXdr = requireArguments().getString(Constant.Bundle.BUNDLE_ENVELOPE_XDR)!!
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
    ): View? {
        _binding = FragmentErrorBinding.inflate(inflater, container, false)
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
                menuInflater.inflate(R.menu.error, menu)
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
        binding.copyXdr.btnCopyXdr.setSafeOnClickListener { mPresenter.copySignedXdrClicked() }
        binding.btnDone.setSafeOnClickListener { mPresenter.doneClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun vibrate(pattern: LongArray) {
        AppUtil.vibrate(requireContext(), pattern)
    }

    override fun setupXdr(xdr: String) {
        binding.copyXdr.tvXdr.text = xdr
    }

    override fun setupErrorInfo(error: String) {
        binding.tvErrorDescription.text = error
    }

    override fun finishScreen() {
        activity?.onBackPressed()
    }

    override fun showHelpScreen(userId: String?) {
        SupportManager.showZendeskHelpCenter(requireContext(), userId = userId)
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
