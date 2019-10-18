package com.lobstr.stellar.vault.presentation.home.transactions.submit_success


import android.os.Bundle
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import kotlinx.android.synthetic.main.fragment_success.*

class SuccessFragment : BaseFragment(), SuccessView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = SuccessFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: SuccessPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideSuccessPresenter() = SuccessPresenter(
        arguments!!.getString(Constant.Bundle.BUNDLE_ENVELOPE_XDR)!!,
        arguments!!.getBoolean(Constant.Bundle.BUNDLE_NEED_ADDITIONAL_SIGNATURES, false)
    )

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_success, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnCopyXdr.setOnClickListener(this)
        btnDone.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.success, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCopyXdr -> mPresenter.copyXdrClicked()
            R.id.btnDone -> mPresenter.doneClicked()
        }
    }

    override fun vibrate(pattern: LongArray) {
        AppUtil.vibrate(context!!, pattern)
    }

    override fun setupXdr(xdr: String) {
        tvXdr.text = xdr
    }

    override fun finishScreen() {
        activity?.onBackPressed()
    }

    override fun setAdditionalSignaturesInfoEnabled(enabled: Boolean) {
        tvAdditionalSignaturesDescription.visibility = if (enabled) View.VISIBLE else View.GONE
        llXdrContainer.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            parentFragment!!.childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, FaqFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
