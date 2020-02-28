package com.lobstr.stellar.vault.presentation.base.activity

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

abstract class BaseActivity : BaseMvpAppCompatActivity(),
    BaseActivityView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = BaseActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: BaseActivityPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    private var mTvToolbarTitle: TextView? = null

    protected var mToolbar: Toolbar? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideBaseActivityPresenter() = BaseActivityPresenter()

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
        // Used for setup padding after change gestures type.
        setWindowInset()
    }

    override fun onBackPressed() {
        val container = supportFragmentManager.findFragmentById(R.id.fl_container)

        // Handle back press in fragment if needed.
        val childFragment = container?.childFragmentManager?.findFragmentById(R.id.fl_container)
        if ((childFragment as? BaseFragment)?.onBackPressed() == true) return

        checkBackPress(container)
    }

    fun checkBackPress(container: Fragment?) {
        val fragmentManager = container?.childFragmentManager ?: supportFragmentManager
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

    override fun setWindowInset() {
        findViewById<View>(R.id.content)?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        findViewById<View>(R.id.content)?.doOnApplyWindowInsets { view, insets, padding ->
            // padding contains the original padding values after inflation
            view.updatePadding(
                bottom = when (this) {
                    is HomeActivity -> 0 // Don't add systemWindowInsetBottom to home screen because BottomNavigationView has it by default.
                    else -> padding.bottom + insets.systemWindowInsetBottom
                },
                top = padding.top + insets.systemWindowInsetTop
            )
        }
    }

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
}
