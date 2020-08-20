package com.lobstr.stellar.vault.presentation.base.activity

import android.app.PendingIntent
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Extra.EXTRA_NOTIFICATION
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : BaseMvpAppCompatActivity(),
    BaseActivityView, TangemDialogFragment.OnTangemDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = BaseActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var daggerPresenter: Lazy<BaseActivityPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private var mTvToolbarTitle: TextView? = null

    protected var mToolbar: Toolbar? = null

    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    val mPresenter: BaseActivityPresenter by moxyPresenter { daggerPresenter.get() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResource())
        findViews()
        setupToolbar()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.getBooleanExtra(EXTRA_NOTIFICATION, false) == true){
            // Reset pin appearance after notification click if needed.
            LVApplication.checkPinAppearance = true
        }
    }

    override fun onResume() {
        super.onResume()
        // Check Pin Appearance.
        when (this) {
            is HomeActivity, is VaultAuthActivity, is ContainerActivity -> {
                if (LVApplication.checkPinAppearance) {
                    LVApplication.checkPinAppearance = false
                    mPresenter.checkPinAppearance()
                }
            }
        }

        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onBackPressed() {
        val container = supportFragmentManager.findFragmentById(R.id.fl_container)

        // Handle back press in fragment if needed.
        val childFragment =
            container?.childFragmentManager?.findFragmentById(R.id.fl_container) ?: container
        if ((childFragment as? BaseFragment)?.onBackPressed() == true) return

        checkBackPress(container)
    }

    fun checkBackPress(container: Fragment?) {
        val fragmentManager =
            (container as? ContainerFragment)?.childFragmentManager ?: supportFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount

        if (backStackCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun setupToolbar() {
        // Set toolbar.
        // Replace actionbar with toolbar.
        setSupportActionBar(mToolbar)
        // Show up btn in actionbar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        // Remove actionbar title.
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Handle HomeAsUpIndicator click.
        mToolbar?.setNavigationOnClickListener { onBackPressed() }
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    //////////////////////////////////////////////////////////////
    // common
    private fun findViews() {
        mToolbar = findViewById(R.id.tb)
        mTvToolbarTitle = mToolbar?.findViewById(R.id.tv_toolbar_title)
    }

    protected abstract fun getLayoutResource(): Int

    ///////////////////////////////////////////////////////////////
    // title

    override fun setActionBarTitle(title: String?) {
        mTvToolbarTitle?.text = title
    }

    override fun setActionBarTitle(@StringRes title: Int) {
        mTvToolbarTitle?.text = if (title != 0) getString(title) else null
    }

    override fun setActionBarTitle(@StringRes title: Int, where: TextUtils.TruncateAt) {
        mTvToolbarTitle?.text = getString(title)
        mTvToolbarTitle?.ellipsize = where
    }

    override fun setActionBarTitle(title: String, where: TextUtils.TruncateAt) {
        mTvToolbarTitle?.text = title
        mTvToolbarTitle?.ellipsize = where
    }

    override fun setActionBarTitle(title: String, @ColorRes color: Int) {
        mTvToolbarTitle?.setTextColor(ContextCompat.getColor(this, color))
        mTvToolbarTitle?.text = title
    }

    override fun setActionBarTitleColor(color: Int) {
        mTvToolbarTitle?.setTextColor(ContextCompat.getColor(this, color))
    }

    ///////////////////////////////////////////////////////////////
    // icon
    override fun setActionBarIcon(iconRes: Int) {
        mToolbar?.setNavigationIcon(iconRes)
    }

    override fun changeActionBarIconVisibility(visible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(visible)
    }

    override fun setActionBarBackground(@DrawableRes background: Int) {
        mToolbar?.setBackgroundResource(background)
    }

    override fun setHomeAsUpIndicator(@DrawableRes image: Int, @ColorRes color: Int) {
        val upArrow = ContextCompat.getDrawable(this, image)
        upArrow?.colorFilter =
            PorterDuffColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
    }

    override fun showTangemScreen(tangemInfo: TangemInfo) {
        TangemDialogFragment().apply {
            arguments =
                Bundle().apply { putParcelable(Constant.Extra.EXTRA_TANGEM_INFO, tangemInfo) }
        }.show(supportFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.TANGEM)
    }

    override fun success(tangemInfo: TangemInfo?) {
        mPresenter.handleTangemInfo(tangemInfo)
    }

    override fun cancel() {
        // Implement logic if needed.
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, supportFragmentManager)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun proceedPinActivityAppearance() {
        startActivity(Intent(this, PinActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.ENTER)
        })
    }
}
