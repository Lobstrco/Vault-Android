package com.lobstr.stellar.vault.presentation.base.activity

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.application.LVApplication
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.container.fragment.ContainerFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.home.app_update.AppUpdateDialogFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.vault_auth.VaultAuthActivity
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
abstract class BaseActivity : BaseMvpAppCompatActivity(),
    BaseActivityView, TangemDialogFragment.OnTangemDialogListener,
    AppUpdateDialogFragment.OnAppUpdateDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @Inject
    lateinit var presenterProvider: Provider<BaseActivityPresenter>

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

    val mPresenter: BaseActivityPresenter by moxyPresenter {
        presenterProvider.get().apply {
            userAccount = intent?.getStringExtra(Constant.Extra.EXTRA_USER_ACCOUNT)
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(
                    Color.TRANSPARENT
                ),
                navigationBarStyle = SystemBarStyle.light(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT
                )
            )
        }

        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        findViews()
        setupToolbar()

        handleInsets()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        PendingIntent.FLAG_CANCEL_CURRENT
        nfcPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )

        onBackPressedDispatcher.addCallback(this) {
            checkBackPress(supportFragmentManager.findFragmentById(R.id.flContainer))
        }
    }

    open fun handleInsets() {
        // Apply common insets for AppbarLayout(left/right) through Toolbar. Top inset appeared from default behaviour.
        handleAppBarInsets()
    }

    private fun handleAppBarInsets() {
        mToolbar?.doOnApplyWindowInsets { view, insets, padding, margins ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            (view.parent as? AppBarLayout)?.also {
                it.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(
                        left = innerPadding.left,
                        right = innerPadding.right,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        mPresenter.onNewIntentCalled(intent?.getStringExtra(Constant.Extra.EXTRA_USER_ACCOUNT))
    }

    override fun onResume() {
        super.onResume()
        // Check Pin Appearance.
        when (this) {
            is HomeActivity, is VaultAuthActivity, is ContainerActivity -> {
                if (LVApplication.checkPinAppearance) {
                    mPresenter.checkPinAppearance()
                }
            }
        }

        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
    }

    override fun onPause() {
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (exc: IllegalStateException) {
            // Handle case when the Activity has already been paused.
            exc.printStackTrace()
        }
        super.onPause()
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
        mToolbar?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
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
        mTvToolbarTitle = mToolbar?.findViewById(R.id.tvToolbarTitle)
    }

    protected abstract fun getContentView(): View

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
    override fun setActionBarIcon(@DrawableRes icon: Int) {
        supportActionBar?.setHomeAsUpIndicator(icon)
    }

    override fun setActionBarIcon(icon: Drawable?) {
        supportActionBar?.setHomeAsUpIndicator(icon)
    }

    override fun setActionBarIcon(@DrawableRes icon: Int, @ColorRes color: Int) {
        val upArrow = ContextCompat.getDrawable(this, icon)
        upArrow?.let {
            val upArrowWrapped = DrawableCompat.wrap(upArrow).mutate()
            DrawableCompat.setTint(upArrowWrapped, ContextCompat.getColor(this, color))
            setActionBarIcon(upArrowWrapped)
        }
    }

    override fun changeActionBarIconVisibility(visible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(visible)
    }

    override fun setActionBarBackground(@DrawableRes background: Int) {
        mToolbar?.setBackgroundResource(background)
    }

    override fun showTangemScreen(show: Boolean, tangemInfo: TangemInfo?) {
        if (show) {
            TangemDialogFragment().apply {
                arguments =
                    Bundle().apply { putParcelable(Constant.Extra.EXTRA_TANGEM_INFO, tangemInfo) }
            }.show(supportFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.TANGEM)
        } else {
            (supportFragmentManager.findFragmentByTag(AlertDialogFragment.DialogFragmentIdentifier.TANGEM) as? DialogFragment)?.dismiss()
        }
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
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.ENTER)
        })
    }

    override fun checkAppVersionBehavior() {
        // Exclude some activities from 'App Version Check' logic.
        when (this) {
            is PinActivity -> {
                /* do nothing*/
            }
            else -> {
                mPresenter.startCheckAppVersion()
            }
        }
    }

    override fun showAppUpdateDialog(
        show: Boolean,
        title: String?,
        message: String?,
        positiveBtnText: String?,
        negativeBtnText: String?
    ) {
        if (show) {
            AlertDialogFragment.Builder(false)
                .setSpecificDialog(AlertDialogFragment.DialogIdentifier.APP_UPDATE)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveBtnText(positiveBtnText)
                .setNegativeBtnText(negativeBtnText)
                .create()
                .showInstant(
                    supportFragmentManager,
                    AlertDialogFragment.DialogFragmentIdentifier.APP_UPDATE
                )
        } else {
            (supportFragmentManager.findFragmentByTag(AlertDialogFragment.DialogFragmentIdentifier.APP_UPDATE) as? DialogFragment)?.dismiss()
        }
    }

    override fun onUpdateClicked() {
        mPresenter.updateAppClicked()
    }

    override fun onSkipClicked() {
        mPresenter.skipAppUpdateClicked()
    }

    override fun showStore(storeUrl: String) {
        AppUtil.openWebPage(this, storeUrl)
    }

    override fun reloadAccountData() {
        when (this) {
            is HomeActivity -> accountWasChanged()
        }
    }
}
