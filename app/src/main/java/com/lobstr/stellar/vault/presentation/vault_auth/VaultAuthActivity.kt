package com.lobstr.stellar.vault.presentation.vault_auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.activity_vault_auth.*

class VaultAuthActivity : BaseActivity(), VaultAuthView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = VaultAuthActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mVaultAuthPresenter: VaultAuthPresenter

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
        setListeners()
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_vault_auth
    }

    private fun setListeners() {
        btnRetry.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnRetry -> mVaultAuthPresenter.tryAuthorizeVault()
        }
    }

    override fun setupToolbar(@ColorRes toolbarColor: Int, @DrawableRes upArrow: Int, @ColorRes upArrowColor: Int) {
        setActionBarBackground(toolbarColor)
        setHomeAsUpIndicator(upArrow, upArrowColor)
        setActionBarTitleColor(upArrowColor)
        changeActionBarIconVisibility(false)
    }

    override fun setBtnRetryVisibility(visible: Boolean) {
        btnRetry.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, supportFragmentManager)
    }

    override fun showSignerInfoFragment() {
        val bundle = Bundle()
        bundle.putInt(Constant.Bundle.BUNDLE_NAVIGATION_FR, Constant.Navigation.SIGNER_INFO)

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            Fragment.instantiate(this, ContainerFragment::class.qualifiedName, bundle),
            R.id.fl_container
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
