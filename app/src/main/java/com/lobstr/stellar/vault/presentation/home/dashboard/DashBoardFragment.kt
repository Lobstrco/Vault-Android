package com.lobstr.stellar.vault.presentation.home.dashboard

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentDashBoardBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.home.HomeActivity
import com.lobstr.stellar.vault.presentation.home.dashboard.account.AccountsDialogFragment
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.adapter.AccountAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.util.*
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class DashboardFragment : BaseFragment(), DashboardView,
    EditAccountDialogFragment.OnEditAccountDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = DashboardFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<DashboardPresenter>

    private var mRefreshAnimation: ObjectAnimator? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter { presenterProvider.get() }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        mPresenter.userVisibleHintCalled(menuVisible)
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenuProvider()
        setListeners()
    }

    private fun addMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.dashboard, menu)
                val refreshView = menu.findItem(R.id.action_refresh).actionView

                mRefreshAnimation = ObjectAnimator.ofFloat(refreshView, View.ROTATION, 0.0f, 360.0f)
                mRefreshAnimation?.duration = 800
                mRefreshAnimation?.repeatCount = 1
                mRefreshAnimation?.interpolator = LinearInterpolator()

                refreshView?.setSafeOnClickListener {
                    mPresenter.refreshClicked()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_refresh -> mPresenter.refreshClicked()
                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setListeners() {
        binding.apply {
            ivMore.setSafeOnClickListener {
                mPresenter.editCurrentAccountClicked()
            }
            flIdentityContainer.setSafeOnClickListener {
                mPresenter.showAccountsClicked()
            }
            tvDashboardPublicKeyTitle.setSafeOnClickListener {
                mPresenter.showAccountsClicked()
            }
            tvDashboardShowList.setSafeOnClickListener {
                mPresenter.showTransactionListClicked()
            }
            tvDashboardCopyPublicKey.setSafeOnClickListener {
                mPresenter.copyKeyClicked()
            }
            tvDashboardTransactionCount.setSafeOnClickListener {
                mPresenter.transactionCountClicked()
            }
            tvDashboardSignersCount.setSafeOnClickListener {
                mPresenter.signersCountClicked()
            }
            tvEmptyStateAction.setSafeOnClickListener {
                mPresenter.addAccountClicked()
            }
            tvAddAccount.setSafeOnClickListener {
                mPresenter.addAccountClicked()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun initSignedAccountsRecycledView() {
        binding.apply {
            rvSignedAccounts.layoutManager = LinearLayoutManager(activity)
            rvSignedAccounts.itemAnimator = null
            rvSignedAccounts.addItemDecoration(
                CustomDividerItemDecoration(
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider_left_right_offset)!!.apply {
                        alpha = 51 // Alpha 0.2.
                    })
            )
            rvSignedAccounts.isNestedScrollingEnabled = false
            rvSignedAccounts.adapter = AccountAdapter(AccountAdapter.ACCOUNT,
                { mPresenter.signedAccountItemClicked(it) },
                { mPresenter.signedAccountItemLongClicked(it) })
        }
    }

    override fun notifySignedAccountsAdapter(accounts: List<Account>) {
        (binding.rvSignedAccounts.adapter as? AccountAdapter)?.setAccountList(accounts)
    }

    override fun showVaultInfo(
        hasTangem: Boolean, identityIconUrl: String, publicKey: String, publicKeyTitle: String
    ) {
        binding.apply {
            ivSignerCard.isVisible = hasTangem
            flIdentityContainer.isVisible = !hasTangem

            if (!hasTangem) {
                tvDashboardPublicKeyTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_down,
                    0
                )
                // Set user identity icon.
                Glide.with(requireContext())
                    .load(identityIconUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(ivIdentity)
            }

            tvDashboardPublicKeyTitle.text = publicKeyTitle
            tvDashboardPublicKey.text = publicKey
        }
    }

    override fun showSignersCount(count: Int) {
        binding.tvDashboardSignersCount.text =
            AppUtil.getQuantityString(R.plurals.text_settings_signers, count, count).run {
                val startPosition = indexOf(count.toString())
                val endPosition = startPosition + count.toString().length
                if (startPosition != Constant.Util.UNDEFINED_VALUE) {
                    this
                        .applyColor(ContextCompat.getColor(
                            requireContext(),
                            R.color.color_primary
                        ), startPosition, endPosition)
                        .applySize(1.5f, startPosition, endPosition)
                        .applyStyle(Typeface.BOLD, startPosition, endPosition)
                } else {
                    this
                }
            }
    }

    override fun showDashboardInfo(count: Int?) {
        binding.apply {
            // Nullable value mean undefined value.
            if (count != null) {
                tvDashboardTransactionCount.text = count.toString()
            }
            pbDashboardTransactions.isVisible = false
            llDashboardTransactionToSignContainer.isVisible = true
        }
    }

    override fun showSignersScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.SIGNED_ACCOUNTS)
        startActivity(intent)
    }

    override fun showSignerInfoScreen() {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.SIGNER_INFO)
        startActivity(intent)
    }

    override fun showErrorMessage(message: String) {
        if (message.isEmpty()) {
            return
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToTransactionList() {
        (activity as? HomeActivity)?.setSelectedBottomNavigationItem(R.id.action_transactions)
    }

    override fun showAccountsDialog() {
        AccountsDialogFragment().show(
            childFragmentManager,
            AlertDialogFragment.DialogFragmentIdentifier.ACCOUNTS
        )
    }

    override fun showEditAccountDialog(address: String) {
        EditAccountDialogFragment().apply {
            arguments = bundleOf(
                Constant.Bundle.BUNDLE_PUBLIC_KEY to address,
                Constant.Bundle.BUNDLE_MANAGE_ACCOUNT_NAME to true
            )
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.EDIT_ACCOUNT)
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
    }

    override fun showSignersProgress(show: Boolean) {
        binding.apply {
            pbDashboardSigners.isVisible = show
            tvAddAccount.isVisible = !show
            tvDashboardSignersCount.isVisible = !show
        }
    }

    override fun showSignersEmptyState(show: Boolean) {
        binding.apply {
            cvDashboardSignersInfo.isVisible = !show
            cvDashboardSignersEmptyState.isVisible = show
            svTransactions.isVisible = !show
        }
    }

    override fun showRefreshAnimation(show: Boolean) {
        if (show) {
            mRefreshAnimation?.start()
        } else {
            mRefreshAnimation?.end()
            mRefreshAnimation?.cancel()
        }
    }

    override fun changeAccountName(name: String) {
        binding.tvDashboardPublicKeyTitle.text = name
    }

    override fun onSetAccountNickNameClicked(publicKey: String) {
        AlertDialogFragment.Builder(true)
            .setSpecificDialog(AlertDialogFragment.DialogIdentifier.ACCOUNT_NAME, Bundle().apply {
                putString(Constant.Bundle.BUNDLE_PUBLIC_KEY, publicKey)
            })
            .create()
            .show(
                childFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.ACCOUNT_NAME
            )
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
