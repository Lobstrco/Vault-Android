package com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic


import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentMnemonicsBinding
import com.lobstr.stellar.vault.presentation.auth.AuthActivity
import com.lobstr.stellar.vault.presentation.auth.mnemonic.confirm_mnemonic.ConfirmMnemonicsFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.mnemonic.MnemonicItem
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

/**
 * Used for show or generate mnemonics
 */
@AndroidEntryPoint
class MnemonicsFragment : BaseFragment(),
    MnemonicsView, AlertDialogFragment.OnDefaultAlertDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentMnemonicsBinding? = null
    private val binding get() = _binding!!
    private var backPressedCallback: OnBackPressedCallback? = null
    private val screenCaptureCallback by lazy {
        Activity.ScreenCaptureCallback {
            mPresenter.screenCaptured()
        }
    }

    @Inject
    lateinit var presenterProvider: Provider<MnemonicsPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get().apply {
        generate = arguments?.getBoolean(Constant.Bundle.BUNDLE_GENERATE_MNEMONICS) ?: false
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
        _binding = FragmentMnemonicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuProvider()
        setListeners()
    }

    private fun addMenuProvider() {
        if (activity is AuthActivity) {
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.mnemonics, menu)
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
    }

    private fun setListeners() {
        backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle back press only for mnemonics in authentication container.
            if (activity is AuthActivity) {
                mPresenter.backPressed()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.apply {
            btnNext.setSafeOnClickListener { mPresenter.nextClicked() }
            btnClipToBoard.setSafeOnClickListener { mPresenter.clipToBordClicked() }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.DETECT_SCREEN_CAPTURE
                    ) == PackageManager.PERMISSION_GRANTED
                ) registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.DETECT_SCREEN_CAPTURE
                    ) == PackageManager.PERMISSION_GRANTED
                ) unregisterScreenCaptureCallback(screenCaptureCallback)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback?.remove()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun setActionLayerVisibility(isVisible: Boolean) {
        binding.llActionLayer.isVisible = isVisible
    }

    override fun setupMnemonics(mnemonicItems: List<MnemonicItem>) {
        binding.apply {
            tvMnemonicsInstruction.text = getString(R.string.mnemonics_instruction, mnemonicItems.size)
            mnemonicContainerView.mMnemonicList = mnemonicItems
            mnemonicContainerView.setupMnemonics()
        }
    }

    override fun showConfirmationScreen(mnemonics: ArrayList<MnemonicItem>) {
        FragmentTransactionManager.displayFragment(
            requireParentFragment().childFragmentManager,
            requireParentFragment().childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader,
                ConfirmMnemonicsFragment::class.qualifiedName!!).apply {
                // Pass created mnemonics to confirmation screen.
                arguments = bundleOf(
                    Constant.Bundle.BUNDLE_MNEMONICS_ARRAY to mnemonics
                )
            },
            R.id.flContainer
        )
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text, isConfidential = true)
    }

    override fun showHelpScreen(articleId: Long) {
        SupportManager.showFreshdeskArticle(requireContext(), articleId)
    }

    override fun showDenyAccountCreationDialog() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.confirmation_title)
            .setMessage(R.string.deny_account_creation_description)
            .setNegativeBtnText(R.string.cancel_action)
            .setPositiveBtnText(R.string.ok_action)
            .create()
            .show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.DENY_ACCOUNT_CREATION)
    }

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mPresenter.onAlertDialogPositiveButtonClicked(tag)
    }

    override fun onNegativeBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onNeutralBtnClick(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun onCancel(tag: String?, dialogInterface: DialogInterface) {
        // Add logic if needed.
    }

    override fun showAuthFr() {
        // Show first fragment in container - AuthFragment.
        parentFragment?.childFragmentManager?.apply {
            popBackStack(
                getBackStackEntryAt(1).id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    override fun showScreenCaptureWarning() {
        AlertDialogFragment.Builder(true)
            .setCancelable(true)
            .setTitle(R.string.screenshot_capture_warning_title)
            .setMessage(R.string.screenshot_capture_warning_description)
            .setPositiveBtnText(R.string.ok_action)
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.INFO
            )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
