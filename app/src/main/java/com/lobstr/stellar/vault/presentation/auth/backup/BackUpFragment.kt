package com.lobstr.stellar.vault.presentation.auth.backup


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic.MnemonicsFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_back_up.*

class BackUpFragment : BaseFragment(), BackUpView, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = BackUpFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: BackUpPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideBackUpPresenter() = BackUpPresenter()

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_back_up, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        ivHelp.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        chbConfirm.setOnCheckedChangeListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivHelp -> mPresenter.helpClicked()
            R.id.btnNext -> mPresenter.nextClicked()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.chbConfirm -> mPresenter.confirmClicked(isChecked)
        }
    }

    override fun showCreateMnemonicsScreen() {
        val bundle = Bundle()
        bundle.putBoolean(Constant.Bundle.BUNDLE_GENERATE_MNEMONICS, true)

        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, MnemonicsFragment::class.java.name, bundle),
            R.id.fl_container,
            true
        )
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, FaqFragment::class.java.name),
            R.id.fl_container,
            true
        )
    }

    override fun setNextBtnEnabled(isEnabled: Boolean) {
        btnNext.isEnabled = isEnabled
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
