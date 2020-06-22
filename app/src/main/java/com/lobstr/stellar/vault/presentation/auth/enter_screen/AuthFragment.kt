package com.lobstr.stellar.vault.presentation.auth.enter_screen


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.backup.BackUpFragment
import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoverKeyFragment
import com.lobstr.stellar.vault.presentation.auth.tangem.TangemSetupFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import kotlinx.android.synthetic.main.fragment_auth.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class AuthFragment : BaseFragment(), AuthFrView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = AuthFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: AuthFrPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideAuthFrPresenter() = AuthFrPresenter()

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_auth, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnAuthNew.setOnClickListener(this)
        btnAuthRestore.setOnClickListener(this)
        btnTangemSignIn.setOnClickListener(this)
        tvHelp.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v?.id) {
            btnAuthNew.id -> mPresenter.newClicked()
            btnAuthRestore.id -> mPresenter.restoreClicked()
            btnTangemSignIn.id -> mPresenter.tangemClicked()
            tvHelp.id -> mPresenter.helpClicked()
        }
    }

    override fun setMovementMethods() {
        tvTermsAndPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showBackUpScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, BackUpFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showRestoreScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, RecoverKeyFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showTangemSetupScreen() {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, TangemSetupFragment::class.qualifiedName!!),
            R.id.fl_container
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
