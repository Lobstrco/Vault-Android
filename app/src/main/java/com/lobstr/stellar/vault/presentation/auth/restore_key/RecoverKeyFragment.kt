package com.lobstr.stellar.vault.presentation.auth.restore_key

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentRecoveryKeyBinding
import com.lobstr.stellar.vault.presentation.auth.restore_key.entities.RecoveryPhraseInfo
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.applyColor
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class RecoverKeyFragment : BaseFragment(), RecoverKeyFrView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = RecoverKeyFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentRecoveryKeyBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<RecoverKeyFrPresenter>

    private var mTextWatcher: TextWatcher? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoveryKeyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuProvider()
        binding.apply {
            etRecoveryPhrase.imeOptions = EditorInfo.IME_ACTION_DONE
            etRecoveryPhrase.setRawInputType(InputType.TYPE_CLASS_TEXT)
        }
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recovery_key, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_info -> mPresenter.infoClicked()
                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            mTextWatcher = etRecoveryPhrase.doAfterTextChanged {
                mPresenter.phrasesChanged(it.toString())
            }
            btnRecoverKey.setSafeOnClickListener { mPresenter.btnRecoverClicked() }
        }
    }

    override fun onPause() {
        super.onPause()

        AppUtil.closeKeyboard(activity)
    }

    override fun onStop() {
        super.onStop()
        binding.etRecoveryPhrase.removeTextChangedListener(mTextWatcher)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showInputErrorIfNeeded(recoveryPhrasesInfo: List<RecoveryPhraseInfo>, phrases: String) {
        for (phrasesInfo in recoveryPhrasesInfo) {
            binding.etRecoveryPhrase.text.applyColor(
                ContextCompat.getColor(requireContext(),
                    if (phrasesInfo.incorrect) android.R.color.holo_red_light else android.R.color.black),
                phrasesInfo.startPosition,
                phrasesInfo.startPosition + phrasesInfo.length
            )
        }
    }

    override fun changeTextBackground(isError: Boolean) {
        binding.etRecoveryPhrase.setBackgroundResource(if (isError) R.drawable.bg_mnemonics_error else R.drawable.bg_mnemonics)
    }

    override fun enableNextButton(enable: Boolean) {
        binding.btnRecoverKey.isEnabled = enable
    }

    override fun showPinScreen() {
        startActivity(Intent(activity, PinActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.CREATE)
        })
    }

    override fun showErrorMessage(@StringRes message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showHelpScreen(articleId: Long) {
        SupportManager.showFreshdeskArticle(requireContext(), articleId)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
