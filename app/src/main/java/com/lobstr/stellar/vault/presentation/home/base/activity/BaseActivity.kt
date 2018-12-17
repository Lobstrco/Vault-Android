package com.lobstr.stellar.vault.presentation.home.base.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity

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
        mTvToolbarTitle?.text = getString(title)
    }

    override fun setActionBarTitle(@StringRes title: Int, where: TextUtils.TruncateAt) {
        mTvToolbarTitle?.text = getString(title)
        mTvToolbarTitle?.ellipsize = where
    }

    override fun setActionBarTitle(title: String, where: TextUtils.TruncateAt) {
        mTvToolbarTitle?.text = title
        mTvToolbarTitle?.ellipsize = where
    }

    override fun setActionBarTitle(title: String, color: Int) {
        mTvToolbarTitle?.setTextColor(color)
        mTvToolbarTitle?.text = title
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
        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(this, background)
        )
    }

}
