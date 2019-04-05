package com.lobstr.stellar.vault.presentation.auth.enter_screen


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.backup.BackUpFragment
import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoverKeyFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import kotlinx.android.synthetic.main.fragment_auth.*

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
        tvHelp.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnAuthNew -> mPresenter.newClicked()
            R.id.btnAuthRestore -> mPresenter.restoreClicked()
            R.id.tvHelp -> mPresenter.helpClicked()
        }
    }

    override fun setMovementMethods() {
        tvTermsAndPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showBackUpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, BackUpFragment::class.qualifiedName),
            R.id.fl_container
        )
    }

    override fun showRestoreScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, RecoverKeyFragment::class.qualifiedName),
            R.id.fl_container
        )
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, FaqFragment::class.qualifiedName),
            R.id.fl_container
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
