package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic


import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_confirm_mnemonics.*

class ConfirmMnemonicsFragment : BaseFragment(), ConfirmMnemonicsView, View.OnClickListener,
    MnemonicsContainerView.MnemonicItemActionListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ConfirmMnemonicsFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: ConfirmMnemonicsPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideConfirmMnemonicsPresenter() = ConfirmMnemonicsPresenter(
        arguments?.getParcelableArrayList(Constant.Bundle.BUNDLE_MNEMONICS_ARRAY)!!
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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_confirm_mnemonics, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btnClear.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        mnemonicContainerToSelectView.setMnemonicItemActionListener(this)
        mnemonicContainerToConfirmView.setMnemonicItemActionListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.confirm_mnemonics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onMnemonicItemClick(v: View, position: Int, value: String) {
        when (v.id) {
            R.id.mnemonicContainerToConfirmView -> mPresenter.mnemonicItemToConfirmClicked(position, value)
            R.id.mnemonicContainerToSelectView -> mPresenter.mnemonicItemToSelectClicked(position, value)
        }
    }

    override fun onMnemonicItemDrag(from: Int, position: Int, value: String) {
        when (from) {
            R.id.mnemonicContainerToConfirmView -> mPresenter.mnemonicItemToConfirmClicked(position, value)
            R.id.mnemonicContainerToSelectView -> mPresenter.mnemonicItemToSelectClicked(position, value)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnClear -> mPresenter.btnClearClicked()
            R.id.btnNext -> mPresenter.btnNextClicked()
        }
    }

    override fun setupMnemonicsToSelect(mnemonics: List<MnemonicItem>) {
        mnemonicContainerToSelectView.mMnemonicList = mnemonics
        mnemonicContainerToSelectView.setupMnemonics()
    }

    override fun setupMnemonicsToConfirm(mnemonics: List<MnemonicItem>) {
        mnemonicContainerToConfirmView.mMnemonicList = mnemonics
        mnemonicContainerToConfirmView.setupMnemonics()
        btnClear.visibility = if (mnemonics.isEmpty()) View.INVISIBLE else View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(@StringRes message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showPinScreen() {
        val intent = Intent(activity, PinActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_CREATE_PIN, true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, FaqFragment::class.qualifiedName),
            R.id.fl_container
        )
    }

    override fun setActionButtonEnabled(enabled: Boolean) {
        btnNext.isEnabled = enabled
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
