package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic.ConfirmMnemonicsFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import kotlinx.android.synthetic.main.fragment_create_mnemonics.*

class CreateMnemonicsFragment : BaseMvpAppCompatFragment(),
    CreateMnemonicsView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = CreateMnemonicsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: CreateMnemonicsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideCreateMnemonicsPresenter() =
        CreateMnemonicsPresenter()

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_create_mnemonics, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnProceed.setOnClickListener(this)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnProceed -> mPresenter.proceedClicked()
        }
    }

    override fun setupMnemonics(mnemonics: List<String>) {
        mnemonicContainerView.mMnemonicList = mnemonics
        mnemonicContainerView.setupMnemonics()
    }

    override fun showConfirmationScreen(mnemonics: CharArray) {
        // Pass created mnemonics to confirmation screen
        val bundle = Bundle()
        bundle.putCharArray(Constant.Bundle.BUNDLE_MNEMONICS_ARRAY, mnemonics)

        FragmentTransactionManager.displayFragment(
            activity!!.supportFragmentManager,
            Fragment.instantiate(context, ConfirmMnemonicsFragment::class.java.name, bundle),
            R.id.fl_auth_content,
            true
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
