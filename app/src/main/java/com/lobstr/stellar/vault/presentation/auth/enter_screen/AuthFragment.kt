package com.lobstr.stellar.vault.presentation.auth.enter_screen


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.backup.BackUpFragment
import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoverKeyFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
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
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnAuthNew -> mPresenter.newClicked()
            R.id.btnAuthRestore -> mPresenter.restoreClicked()
        }
    }

    override fun setMovementMethods() {
        tvTermsAndPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showBackUpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, BackUpFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun showRestoreScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, RecoverKeyFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
