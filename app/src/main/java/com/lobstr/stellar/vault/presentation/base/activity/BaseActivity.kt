package com.lobstr.stellar.vault.presentation.base.activity

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil

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
    }

    override fun onBackPressed() {
        checkBackPress(supportFragmentManager.findFragmentById(R.id.fl_container))
    }

    protected fun checkBackPress(container: Fragment?) {
        AppUtil.closeKeyboard(this)
        val fragmentManager = container?.childFragmentManager ?: supportFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount

        if (backStackCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun setupToolbar() {
        // set toolbar
        // replace actionbar with toolbar
        setSupportActionBar(mToolbar)
        // show up btn in actionbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        // remove actionbar title
        supportActionBar?.setDisplayShowTitleEnabled(false)
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

    override fun setActionBarTitle(title: String) {
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
        upArrow?.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
    }
}
