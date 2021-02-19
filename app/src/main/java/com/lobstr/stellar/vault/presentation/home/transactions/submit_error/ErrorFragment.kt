package com.lobstr.stellar.vault.presentation.home.transactions.submit_error


import android.os.Bundle
import android.view.*
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentErrorBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ErrorFragment : BaseFragment(), ErrorView, View.OnClickListener {

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

    private val mPresenter by moxyPresenter { presenterProvider.get().apply {
            error = requireArguments().getString(Constant.Bundle.BUNDLE_ERROR_MESSAGE)!!
        }}

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
        setListeners()
    }

    private fun setListeners() {
        binding.btnDone.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.error, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
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
            binding.btnDone.id -> mPresenter.doneClicked()
        }
    }

    override fun vibrate(pattern: LongArray) {
        AppUtil.vibrate(requireContext(), pattern)
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

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
