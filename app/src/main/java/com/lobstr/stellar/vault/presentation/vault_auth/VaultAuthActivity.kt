package com.lobstr.stellar.vault.presentation.vault_auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.vault_auth.recheck_signer.RecheckSignerFragment
import com.lobstr.stellar.vault.presentation.vault_auth.signer_info.SignerInfoFragment
import kotlinx.android.synthetic.main.activity_vault_auth.*

class VaultAuthActivity : BaseMvpAppCompatActivity(), VaultAuthView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = AuthActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: VaultAuthPresenter

    private var mProgressDialog: AlertDialogFragment? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideVaultAuthPresenter() = VaultAuthPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault_auth)
        setListeners()
    }

    private fun setListeners() {
        btnRetry.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnRetry -> mPresenter.tryAuthorizeVault()
        }
    }

    override fun showProgressDialog() {
        mProgressDialog = ProgressManager.show(this, false)
    }

    override fun dismissProgressDialog() {
        ProgressManager.dismiss(mProgressDialog)
    }

    override fun showSignerInfoFragment(userPublicKey: String) {
        btnRetry.visibility = View.GONE
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, userPublicKey)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, SignerInfoFragment::class.java.name, bundle),
            R.id.fl_vault_auth_content,
            false
        )
    }

    override fun showRecheckSignerFragment(userPublicKey: String) {
        btnRetry.visibility = View.GONE
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, userPublicKey)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, RecheckSignerFragment::class.java.name, bundle),
            R.id.fl_vault_auth_content,
            false
        )
    }

    override fun showHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
