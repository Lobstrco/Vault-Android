package com.lobstr.stellar.vault.presentation.pin

import android.content.Intent
import android.os.Bundle
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.pin.main.PinFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.PinMode.ENTER
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import moxy.ktx.moxyPresenter

class PinActivity : BaseActivity(), PinView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = PinActivity::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPinPresenter by moxyPresenter { PinPresenter(
        intent?.getByteExtra(Constant.Extra.EXTRA_PIN_MODE, ENTER) ?: ENTER
    ) }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun getLayoutResource(): Int {
        return R.layout.activity_pin
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showPinFr(pinMode: Byte) {
        val bundle = Bundle()
        bundle.putByte(Constant.Bundle.BUNDLE_PIN_MODE, pinMode)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            this.classLoader,
            PinFragment::class.qualifiedName!!
        )
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            supportFragmentManager,
            fragment,
            R.id.fl_container,
            false
        )
    }

    override fun onBackPressed() {
        mPinPresenter.onBackPressed()
    }

    override fun finishApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun finishScreen() {
        finish()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
