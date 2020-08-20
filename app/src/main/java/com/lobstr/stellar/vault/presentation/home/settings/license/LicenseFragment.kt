package com.lobstr.stellar.vault.presentation.home.settings.license


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_license.*
import moxy.ktx.moxyPresenter

class LicenseFragment : BaseFragment(), LicenseView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = LicenseFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { LicensePresenter() }

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_license, container, false) else mView
        return mView
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setupPagePath(path: String) {
        wvLicense.loadUrl(path)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
