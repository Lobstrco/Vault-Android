package com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic


import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentConfirmMnemonicsBinding
import com.lobstr.stellar.vault.presentation.auth.mnemonic.MnemonicsContainerView
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.pin.PinActivity
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
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

    private var _binding: FragmentConfirmMnemonicsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<ConfirmMnemonicsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get().apply {
        mnemonicsInitialList = arguments?.getParcelableArrayList(Constant.Bundle.BUNDLE_MNEMONICS_ARRAY)!!
    } }

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
        _binding = FragmentConfirmMnemonicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnClear.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.mnemonicContainerToSelectView.setMnemonicItemActionListener(this)
        binding.mnemonicContainerToConfirmView.setMnemonicItemActionListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.confirm_mnemonics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> mPresenter.infoClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        when (v?.id) {
            binding.btnClear.id -> mPresenter.btnClearClicked()
            binding.btnNext.id -> mPresenter.btnNextClicked()
        }
    }

    override fun setupMnemonicsToSelect(mnemonics: List<MnemonicItem>) {
        binding.mnemonicContainerToSelectView.mMnemonicList = mnemonics
        binding.mnemonicContainerToSelectView.setupMnemonics()
    }

    override fun setupMnemonicsToConfirm(mnemonics: List<MnemonicItem>) {
        binding.mnemonicContainerToConfirmView.mMnemonicList = mnemonics
        binding.mnemonicContainerToConfirmView.setupMnemonics()
        binding.btnClear.visibility = if (mnemonics.isEmpty()) View.INVISIBLE else View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(@StringRes message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun showPinScreen() {
        startActivity(Intent(activity, PinActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constant.Extra.EXTRA_PIN_MODE, Constant.PinMode.CREATE)
        })
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showHelpScreen(articleId: Long) {
        SupportManager.showZendeskArticle(requireContext(), articleId)
    }

    override fun setActionButtonEnabled(enabled: Boolean) {
        binding.btnNext.isEnabled = enabled
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
