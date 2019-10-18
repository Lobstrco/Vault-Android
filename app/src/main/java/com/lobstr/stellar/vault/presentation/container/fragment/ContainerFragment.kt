package com.lobstr.stellar.vault.presentation.container.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.auth.biometric.BiometricSetUpFragment
import com.lobstr.stellar.vault.presentation.auth.enter_screen.AuthFragment
import com.lobstr.stellar.vault.presentation.auth.mnemonic.create_mnemonic.MnemonicsFragment
import com.lobstr.stellar.vault.presentation.base.fragment.BaseContainerFragment
import com.lobstr.stellar.vault.presentation.entities.transaction.TransactionItem
import com.lobstr.stellar.vault.presentation.home.dashboard.DashboardFragment
import com.lobstr.stellar.vault.presentation.home.settings.SettingsFragment
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.SignedAccountsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.TransactionsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.details.TransactionDetailsFragment
import com.lobstr.stellar.vault.presentation.home.transactions.import_xdr.ImportXdrFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_error.ErrorFragment
import com.lobstr.stellar.vault.presentation.home.transactions.submit_success.SuccessFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.Constant.Bundle.BUNDLE_NAVIGATION_FR
import com.lobstr.stellar.vault.presentation.util.manager.FragmentTransactionManager
import com.lobstr.stellar.vault.presentation.vault_auth.signer_info.SignerInfoFragment

/**
 * Used for containing all main fragments in the app
 * and manage common toolbar: title, up button and other
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

    @InjectPresenter
    lateinit var mPresenter: ContainerPresenter

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * BUNDLE_NAVIGATION_FR - flag for setup main (initial) fragment in container
     * @see Constant.Navigation
     */
    @ProvidePresenter
    fun provideContainerPresenter() =
        ContainerPresenter(
            arguments?.getInt(BUNDLE_NAVIGATION_FR)!!,
            arguments?.getParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM),
            arguments?.getString(Constant.Bundle.BUNDLE_ENVELOPE_XDR),
            arguments?.getBoolean(Constant.Bundle.BUNDLE_NEED_ADDITIONAL_SIGNATURES, false),
            arguments?.getString(Constant.Bundle.BUNDLE_ERROR_MESSAGE)
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
        return inflater.inflate(R.layout.fragment_container, container, false)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showAuthFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, AuthFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showSignerInfoFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, SignerInfoFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showBiometricSetUpFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, BiometricSetUpFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    // base unique home tabs fragment
    override fun showDashBoardFr() {
        // unique fragment must be first in container
        if (childFragmentManager.findFragmentById(R.id.fl_container) != null) return
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, DashboardFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    // base unique home tabs fragment
    override fun showSettingsFr() {
        // unique fragment must be first in container
        if (childFragmentManager.findFragmentById(R.id.fl_container) != null) return
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, SettingsFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    // base unique home tabs fragment
    override fun showTransactionsFr() {
        // unique fragment must be first in container
        if (childFragmentManager.findFragmentById(R.id.fl_container) != null) return
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, TransactionsFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showTransactionDetails(target: Fragment?, transactionItem: TransactionItem) {
        val bundle = Bundle()
        bundle.putParcelable(Constant.Bundle.BUNDLE_TRANSACTION_ITEM, transactionItem)
        val fragment = childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, TransactionDetailsFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        if (target != null) {
            fragment.setTargetFragment(target, Constant.Code.TRANSACTION_DETAILS_FRAGMENT)
        }

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showImportXdrFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, ImportXdrFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showMnemonicsFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, MnemonicsFragment::class.qualifiedName!!),
            R.id.fl_container
        )
    }

    override fun showSuccessFr(envelopeXdr: String, needAdditionalSignatures: Boolean) {
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_ENVELOPE_XDR, envelopeXdr)
        bundle.putBoolean(Constant.Bundle.BUNDLE_NEED_ADDITIONAL_SIGNATURES, needAdditionalSignatures)
        val fragment = childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, SuccessFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showErrorFr(errorMessage: String) {
        val bundle = Bundle()
        bundle.putString(Constant.Bundle.BUNDLE_ERROR_MESSAGE, errorMessage)
        val fragment = childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, ErrorFragment::class.qualifiedName!!)
        fragment.arguments = bundle

        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            fragment,
            R.id.fl_container
        )
    }

    override fun showSignedAccountsFr() {
        FragmentTransactionManager.displayFragment(
            childFragmentManager,
            childFragmentManager.fragmentFactory.instantiate(context!!.classLoader, SignedAccountsFragment::class.qualifiedName!!),
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
