package com.lobstr.stellar.vault.presentation.home.transactions.submit_error


import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentErrorBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.util.*
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ErrorFragment : BaseFragment(), ErrorView {

    // ===========================================================
    // Constants
    // ===========================================================

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
            error = requireArguments().parcelable(Constant.Bundle.BUNDLE_ERROR)!!
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
        binding.apply {
            btnViewDetails.setSafeOnClickListener { mPresenter.viewErrorDetailsClicked() }
            copyXdr.btnCopyXdr.setSafeOnClickListener { mPresenter.copySignedXdrClicked() }
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

    override fun setupErrorInfo(error: String, showViewDetails: Boolean) {
        binding.apply {
            tvErrorDescription.text = error
            btnViewDetails.isVisible = showViewDetails
        }
    }

    override fun finishScreen() {
        activity?.onBackPressedDispatcher?.onBackPressed()
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

    override fun showErrorDetails(details: String) {
        AlertDialogFragment.Builder(true)
            .setCancelable(false)
            .setTitle(R.string.operation_error_details_title)
            .setMessage(details)
            .setNegativeBtnText(R.string.close_action)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.INFO
            )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
