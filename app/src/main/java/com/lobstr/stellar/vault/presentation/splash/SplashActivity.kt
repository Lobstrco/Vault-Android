package com.lobstr.stellar.vault.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatActivity
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SplashActivity : BaseMvpAppCompatActivity(), SplashView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SplashActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: SplashPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideSplashPresenter() = SplashPresenter()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Used for setup padding after change gestures type.
        setWindowInset()
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setWindowInset() {
        window.decorView.findViewById<View>(android.R.id.content)?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        window.decorView.findViewById<View>(android.R.id.content)
            ?.doOnApplyWindowInsets { view, insets, padding ->
                // Padding contains the original padding values after inflation.
                view.updatePadding(
                    bottom = padding.bottom + insets.systemWindowInsetBottom,
                    top = padding.top + insets.systemWindowInsetTop
                )
            }
    }

    override fun showAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.AUTH)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showPinScreen() {
        val intent = Intent(this, PinActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
