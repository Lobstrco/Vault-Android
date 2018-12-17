package com.lobstr.stellar.vault.presentation.auth.enter_screen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic.CreateMnemonicsFragment
import com.lobstr.stellar.vault.presentation.auth.restore_key.RecoveryKeyFragment
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : BaseMvpAppCompatFragment(), AuthFrView, View.OnClickListener {

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

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnAuthNew -> mPresenter.newClicked()
            R.id.btnAuthRestore -> mPresenter.restoreClicked()
        }
    }

    override fun showCreateMnemonicsScreen() {
        FragmentTransactionManager.displayFragment(
            activity!!.supportFragmentManager,
            Fragment.instantiate(context, CreateMnemonicsFragment::class.java.name),
            R.id.fl_auth_content,
            true
        )
    }

    override fun showRestoreScreen() {
        FragmentTransactionManager.displayFragment(
            activity!!.supportFragmentManager,
            Fragment.instantiate(context, RecoveryKeyFragment::class.java.name),
            R.id.fl_auth_content,
            true
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private fun setListeners() {
        btnAuthNew.setOnClickListener(this)
        btnAuthRestore.setOnClickListener(this)
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
