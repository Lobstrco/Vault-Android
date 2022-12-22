package com.lobstr.stellar.vault.presentation.home.account_name.manage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentManageAccountsNamesBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.container.activity.ContainerActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.account.Account
import com.lobstr.stellar.vault.presentation.home.account_name.manage.adapter.AccountNameAdapter
import com.lobstr.stellar.vault.presentation.home.settings.signed_accounts.edit_account.EditAccountDialogFragment
import com.lobstr.stellar.vault.presentation.util.AppUtil
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.CustomDividerItemDecoration
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ManageAccountsNamesFragment : BaseFragment(), ManageAccountsNamesView,
    EditAccountDialogFragment.OnEditAccountDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ManageAccountsNamesFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentManageAccountsNamesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<ManageAccountsNamesPresenter>

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
        _binding = FragmentManageAccountsNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            fabAddAccountName.setSafeOnClickListener {
                mPresenter.addAccountNameClicked()
            }
            srlAccountsNames.setOnRefreshListener {
                mPresenter.onRefreshCalled()
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

    override fun setupToolbarTitle(titleRes: Int) {
        saveActionBarTitle(titleRes)
    }

    override fun initRecycledView() {
        binding.apply {
            rvAccountsNames.layoutManager = LinearLayoutManager(activity)
            rvAccountsNames.addItemDecoration(CustomDividerItemDecoration(
                ContextCompat.getDrawable(requireContext(), R.drawable.divider_left_right_offset)!!
                    .apply {
                        alpha = 51 // Alpha 0.2.
                    }))

            rvAccountsNames.adapter = AccountNameAdapter(
                { mPresenter.accountItemClicked(it) },
                { mPresenter.accountItemLongClicked(it) })
        }
    }

    override fun showAccountsNamesList(items: List<Account>?) {
        (binding.rvAccountsNames.adapter as? AccountNameAdapter)?.submitList(items)
    }

    override fun scrollListToPosition(position: Int) {
        binding.rvAccountsNames.scrollToPosition(position)
    }

    override fun showProgress(show: Boolean) {
        binding.srlAccountsNames.isRefreshing = show
    }

    override fun showEditAccountDialog(address: String) {
        EditAccountDialogFragment().apply {
            arguments = bundleOf(
                Constant.Bundle.BUNDLE_PUBLIC_KEY to address,
                Constant.Bundle.BUNDLE_MANAGE_ACCOUNT_NAME to true
            )
        }.show(childFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.EDIT_ACCOUNT)
    }

    override fun showEmptyState(show: Boolean) {
        binding.tvAccountsNamesEmptyState.isVisible = show
    }

    override fun copyToClipBoard(text: String) {
        AppUtil.copyToClipboard(context, text)
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

    override fun showAddAccountNameScreen() {
        startActivity(Intent(context, ContainerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(Constant.Extra.EXTRA_NAVIGATION_FR, Constant.Navigation.ADD_ACCOUNT_NAME)
        })
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}