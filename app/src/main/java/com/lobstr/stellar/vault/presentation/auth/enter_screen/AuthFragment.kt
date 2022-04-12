package com.lobstr.stellar.vault.presentation.auth.enter_screen


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentAuthBinding
import com.lobstr.stellar.vault.presentation.auth.backup.BackUpFragment
import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoverKeyFragment
import com.lobstr.stellar.vault.presentation.auth.tangem.TangemSetupFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import moxy.ktx.moxyPresenter

class AuthFragment : BaseFragment(), AuthFrView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = AuthFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { AuthFrPresenter() }

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
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnAuthNew.setSafeOnClickListener { mPresenter.newClicked() }
        binding.btnAuthRestore.setSafeOnClickListener { mPresenter.restoreClicked() }
        binding.btnTangemSignIn.setSafeOnClickListener { mPresenter.tangemClicked() }
        binding.tvHelp.setSafeOnClickListener { mPresenter.helpClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setMovementMethods() {
        binding.tvTermsAndPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showBackUpScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, BackUpFragment::class.qualifiedName!!),
            R.id.flContainer
        )
    }

    override fun showRestoreScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, RecoverKeyFragment::class.qualifiedName!!),
            R.id.flContainer
        )
    }

    override fun showTangemSetupScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, TangemSetupFragment::class.qualifiedName!!),
            R.id.flContainer
        )
    }

    override fun showHelpScreen() {
        SupportManager.showZendeskHelpCenter(requireContext())
    }

    override fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
