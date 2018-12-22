package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.BaseMvpAppCompatFragment
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_confirm_mnemonics.*

class ConfirmMnemonicsFragment : BaseMvpAppCompatFragment(), ConfirmMnemonicsView, View.OnClickListener,
    MnemonicsContainerView.MnemonicItemClickListener {

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

    private var mProgressDialog: AlertDialogFragment? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideConfirmMnemonicsPresenter() = ConfirmMnemonicsPresenter(
        arguments?.getCharArray(Constant.Bundle.BUNDLE_MNEMONICS_ARRAY)!!
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
        btnConfirm.setOnClickListener(this)
        mnemonicContainerToSelectView.setMnemonicItemClickListener(this)
        mnemonicContainerToConfirmView.setMnemonicItemClickListener(this)
    }

    override fun onMnemonicItemClick(v: View, position: Int, value: String) {
        when (v.id) {
            R.id.mnemonicContainerToConfirmView -> mPresenter.mnemonicItemToConfirmClicked(position, value)
            R.id.mnemonicContainerToSelectView -> mPresenter.mnemonicItemToSelectClicked(position, value)
        }
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnConfirm -> mPresenter.confirmClicked()
        }
    }

    override fun setupMnemonicsToSelect(mnemonics: List<String>) {
        btnConfirm.isEnabled = mnemonics.isEmpty()
        tvSelectToEmptyState.visibility = if (mnemonics.isEmpty()) View.VISIBLE else View.GONE
        mnemonicContainerToSelectView.mMnemonicList = mnemonics
        mnemonicContainerToSelectView.setupMnemonics()
    }

    override fun setupMnemonicsToConfirm(mnemonics: List<String>) {
        tvConfirmToEmptyState.visibility = if (mnemonics.isEmpty()) View.VISIBLE else View.GONE
        mnemonicContainerToConfirmView.mMnemonicList = mnemonics
        mnemonicContainerToConfirmView.setupMnemonics()
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

    override fun showProgressDialog() {
        mProgressDialog = ProgressManager.show(activity as? AppCompatActivity, false)
    }

    override fun dismissProgressDialog() {
        ProgressManager.dismiss(mProgressDialog)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
