package com.lobstr.stellar.vault.presentation.home.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.os.bundleOf
import com.google.android.material.tabs.TabLayoutMediator
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentTransactionsContainerBinding
import com.lobstr.stellar.vault.presentation.FragmentViewPagerAdapter
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TransactionsContainerFragment : BaseFragment(), TransactionsContainerView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentTransactionsContainerBinding? = null
    private val binding get() = _binding!!

    val mCommonRegisterForTransactionResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            childFragmentManager.fragments.filterIsInstance<TransactionsFragment>()
                .forEach { fr -> fr.handleTransactionResult(result) }
        }

    @Inject
    lateinit var presenterProvider: Provider<TransactionsContainerPresenter>

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
    ): View? {
        _binding = FragmentTransactionsContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initViewPager() {
        binding.apply {
            vpData.adapter = null
            vpData.offscreenPageLimit = 2
            vpData.adapter = FragmentViewPagerAdapter(childFragmentManager, lifecycle).apply {
                addFragment(
                    TransactionsFragment().apply {
                        arguments = bundleOf(
                            Constant.Bundle.BUNDLE_SHOW_COMMON_TOOLBAR to false,
                            Constant.Bundle.BUNDLE_TRANSACTIONS_STATUS to Constant.Transaction.Status.PENDING
                        )
                    },
                    AppUtil.getString(R.string.status_pending_title)
                )
                addFragment(
                    TransactionsFragment().apply {
                        arguments = bundleOf(
                            Constant.Bundle.BUNDLE_SHOW_COMMON_TOOLBAR to false,
                            Constant.Bundle.BUNDLE_TRANSACTIONS_STATUS to Constant.Transaction.Status.SIGNED
                        )
                    },
                    AppUtil.getString(R.string.status_signed_title)
                )
            }
            TabLayoutMediator(tlTabs, vpData) { tab, position ->
                tab.text = (vpData.adapter as? FragmentViewPagerAdapter)?.getPageTitle(position)
            }.attach()
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}