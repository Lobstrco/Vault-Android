package com.lobstr.stellar.vault.presentation.container.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.enter_screen.AuthFragment
import com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic.MnemonicsFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseContainerFragment
import com.lobstr.stellar.vault.presentation.entities.error.Error
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.account_name.add.AddAccountNameFragment
import com.lobstr.stellar.vault.presentation.home.dashboard.DashboardFragment
import com.lobstr.stellar.vault.presentation.home.settings.SettingsFragment
import com.lobstr.stellar.vault.presentation.home.settings.config.ConfigFragment
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.SignedAccountsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.TransactionsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.import_xdr.ImportXdrFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_NAVIGATION_FR
import com.lobstr.stellar.vault.presentation.util.Constant.TransactionConfirmationSuccessStatus.SUCCESS
import com.lobstr.stellar.vault.presentation.util.Constant.Util.UNDEFINED_VALUE
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.vault_auth.auth.VaultAuthFragment
import com.lobstr.stellar.vault.presentation.vault_auth.signer_info.SignerInfoFragment
import moxy.ktx.moxyPresenter

/**
 * Used for containing all main fragments in the app
 * and manage common toolbar: title, up button and other.
 */
class ContainerFragment : BaseContainerFragment(),
    ContainerView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ContainerFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * BUNDLE_NAVIGATION_FR - flag for setup main (initial) fragment in container.
     * @see Constant.Navigation
     */
    private val mPresenter by moxyPresenter {
        ContainerPresenter(
            arguments?.getInt(BUNDLE_NAVIGATION_FR)!!,
            arguments?.getParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM),
            arguments?.getString(Constant.Bundle.BUNDLE_ENVELOPE_XDR),
            arguments?.getByte(
                Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS,
                SUCCESS
            ),
            arguments?.getParcelable(Constant.Bundle.BUNDLE_ERROR),
            arguments?.getInt(Constant.Bundle.BUNDLE_CONFIG, UNDEFINED_VALUE) ?: UNDEFINED_VALUE
        )
    }

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
        return inflater.inflate(R.layout.fragment_container, container, false)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showAuthFr() {
        displayFragment(
            fragmentName = AuthFragment::class.qualifiedName!!
        )
    }

    override fun showVaultAuthFr() {
        displayFragment(
            fragmentName = VaultAuthFragment::class.qualifiedName!!
        )
    }

    override fun showSignerInfoFr() {
        displayFragment(
            fragmentName = SignerInfoFragment::class.qualifiedName!!
        )
    }

    // Base unique home tabs fragment.
    override fun showDashBoardFr() {
        displayFragment(
            fragmentName = DashboardFragment::class.qualifiedName!!,
            checkIsUnique = true
        )
    }

    // Base unique home tabs fragment.
    override fun showSettingsFr() {
        displayFragment(
            fragmentName = SettingsFragment::class.qualifiedName!!,
            checkIsUnique = true
        )
    }

    // Base unique home tabs fragment.
    override fun showTransactionsFr() {
        displayFragment(
            fragmentName = TransactionsFragment::class.qualifiedName!!,
            checkIsUnique = true
        )
    }

    override fun showTransactionDetails(transactionItem: TransactionItem) {
        displayFragment(
            fragmentName = TransactionDetailsFragment::class.qualifiedName!!,
            bundle = bundleOf(Constant.Bundle.BUNDLE_TRANSACTION_ITEM to transactionItem)
        )
    }

    override fun showImportXdrFr() {
        displayFragment(
            fragmentName = ImportXdrFragment::class.qualifiedName!!
        )
    }

    override fun showMnemonicsFr() {
        displayFragment(
            fragmentName = MnemonicsFragment::class.qualifiedName!!
        )
    }

    override fun showSuccessFr(envelopeXdr: String, transactionSuccessStatus: Byte) {
        displayFragment(
            fragmentName = SuccessFragment::class.qualifiedName!!,
            bundle = bundleOf(
                Constant.Bundle.BUNDLE_ENVELOPE_XDR to envelopeXdr,
                Constant.Bundle.BUNDLE_TRANSACTION_CONFIRMATION_SUCCESS_STATUS to transactionSuccessStatus
            )
        )
    }

    override fun showErrorFr(error: Error) {
        displayFragment(
            fragmentName = ErrorFragment::class.qualifiedName!!,
            bundle = bundleOf(
                Constant.Bundle.BUNDLE_ERROR to error,
            )
        )
    }

    override fun showSignedAccountsFr() {
        displayFragment(
            fragmentName = SignedAccountsFragment::class.qualifiedName!!
        )
    }

    override fun showConfigFr(config: Int) {
        displayFragment(
            fragmentName = ConfigFragment::class.qualifiedName!!,
            bundle = bundleOf(Constant.Bundle.BUNDLE_CONFIG to config)
        )
    }

    override fun showAddAccountNameFr() {
        displayFragment(
            fragmentName = AddAccountNameFragment::class.qualifiedName!!,
        )
    }

    private fun displayFragment(
        fragmentName: String,
        bundle: Bundle? = null,
        checkIsUnique: Boolean = false
    ) {
        if (checkIsUnique) {
            // Unique fragment must be first in container.
            if (childFragmentManager.findFragmentById(R.id.flContainer) != null) return
        }
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(
                requireContext().classLoader,
                fragmentName
            ).apply {
                arguments = bundle
            },
            R.id.flContainer
        )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
