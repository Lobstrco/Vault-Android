package com.lobstr.stellar.vault.presentation.auth.restore_key

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.fusechain.digitalbits.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.RecoveryPhraseInfo
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.faq.FaqFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_recovery_key.*

class RecoveryKeyFragment : BaseFragment(), RecoveryKeyFrView, View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = RecoveryKeyFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: RecoveryKeyFrPresenter

    private var mView: View? = null

    private var mProgressDialog: AlertDialogFragment? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideRecoveryKeyFrPresenter() = RecoveryKeyFrPresenter()

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
        mView = if (mView == null) inflater.inflate(R.layout.fragment_recovery_key, container, false) else mView
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etRecoveryPhrase.imeOptions = EditorInfo.IME_ACTION_DONE
        etRecoveryPhrase.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    override fun onResume() {
        super.onResume()

        setListeners()
    }

    private fun setListeners() {
        etRecoveryPhrase.addTextChangedListener(mTextWatcher)
        btnRecoveryKey.setOnClickListener(this)
    }

    override fun onPause() {
        super.onPause()

        AppUtil.closeKeyboard(activity)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.recovery_key, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    private var mTextWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            mPresenter.phrasesChanged(etRecoveryPhrase?.text.toString())
        }
    }

    override fun onStop() {
        super.onStop()

        etRecoveryPhrase.removeTextChangedListener(mTextWatcher)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnRecoveryKey -> mPresenter.btnRecoveryClicked()
        }
    }

    override fun showInputErrorIfNeeded(recoveryPhrasesInfo: List<RecoveryPhraseInfo>, phrases: String) {
        for (phrasesInfo in recoveryPhrasesInfo) {
            val color = if (phrasesInfo.incorrect) {
                ForegroundColorSpan(ContextCompat.getColor(context!!, android.R.color.holo_red_light))
            } else {
                ForegroundColorSpan(ContextCompat.getColor(context!!, android.R.color.black))
            }

            etRecoveryPhrase.text.setSpan(
                color,
                phrasesInfo.startPosition,
                phrasesInfo.startPosition + phrasesInfo.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun changeTextBackground(isError: Boolean) {
        if (isError) {
            etRecoveryPhrase.setBackgroundResource(R.drawable.bg_mnemonics_error)
        } else {
            etRecoveryPhrase.setBackgroundResource(R.drawable.bg_mnemonics)
        }
    }

    override fun enableNextButton(enable: Boolean) {
        btnRecoveryKey.isEnabled = enable
    }

    override fun showPinScreen() {
        val intent = Intent(activity, PinActivity::class.java)
        intent.putExtra(Constant.Extra.EXTRA_CREATE_PIN, true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showErrorMessage(@StringRes message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog() {
        mProgressDialog = ProgressManager.show(activity as? AppCompatActivity, false)
    }

    override fun dismissProgressDialog() {
        ProgressManager.dismiss(mProgressDialog)
    }

    override fun showHelpScreen() {
        FragmentTransactionManager.displayFragment(
            parentFragment!!.childFragmentManager,
            Fragment.instantiate(context, FaqFragment::class.java.name),
            R.id.fl_container,
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
