package com.lobstr.stellar.vault.presentation.tangem

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.ActivityTangemCreateWalletBinding
import com.lobstr.stellar.vault.presentation.base.activity.BaseActivity
import com.lobstr.stellar.vault.presentation.dialog.alert.base.AlertDialogFragment
import com.lobstr.stellar.vault.presentation.entities.tangem.TangemInfo
import com.lobstr.stellar.vault.presentation.tangem.dialog.TangemDialogFragment
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.doOnApplyWindowInsets
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import com.lobstr.stellar.vault.presentation.util.parcelableExtra
import com.lobstr.stellar.vault.presentation.util.setSafeOnClickListener
import moxy.ktx.moxyPresenter

class TangemCreateWalletActivity : BaseActivity(), TangemCreateWalletView,
    AlertDialogFragment.OnDefaultAlertDialogListener, TangemDialogFragment.OnTangemDialogListener {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private lateinit var binding: ActivityTangemCreateWalletBinding

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mCreateWalletPresenter by moxyPresenter { TangemCreateWalletPresenter(intent?.parcelableExtra(Constant.Extra.EXTRA_TANGEM_INFO)) }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addMenuProvider()
        setListeners()
    }

    override fun handleInsets() {
        super.handleInsets()
        binding.flContainer.doOnApplyWindowInsets { view, insets, padding, _ ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(
                left = padding.left + innerPadding.left,
                right = padding.right + innerPadding.right,
                bottom = padding.bottom + innerPadding.bottom
            )
        }
    }

    private fun addMenuProvider() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tangem_create_wallet, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_info -> mCreateWalletPresenter.infoClicked()
                    else -> return false
                }
                return true
            }
        })
    }

    override fun getContentView(): View {
        binding = ActivityTangemCreateWalletBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun setListeners() {
        binding.btnCreateWallet.setSafeOnClickListener { mCreateWalletPresenter.createWalletClicked() }
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun showNfcCheckDialog() {
        AlertDialogFragment.Builder(false)
            .setCancelable(true)
            .setTitle(R.string.nfc_enable_title)
            .setMessage(getString(R.string.nfc_enable_description))
            .setNegativeBtnText(R.string.cancel_action)
            .setPositiveBtnText(R.string.ok_action)
            .create()
            .show(
                supportFragmentManager,
                AlertDialogFragment.DialogFragmentIdentifier.NFC_INFO_DIALOG
            )
    }

    override fun onPositiveBtnClick(tag: String?, dialogInterface: DialogInterface) {
        mCreateWalletPresenter.onAlertDialogPositiveButtonClicked(tag)
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

    override fun showNfcDeviceSettings() {
        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
    }

    override fun showTangemScreen(tangemInfo: TangemInfo) {
        TangemDialogFragment().apply {
            this.arguments =
                Bundle().apply { putParcelable(Constant.Extra.EXTRA_TANGEM_INFO, tangemInfo) }
        }.show(supportFragmentManager, AlertDialogFragment.DialogFragmentIdentifier.TANGEM)
    }

    override fun success(tangemInfo: TangemInfo?) {
        mCreateWalletPresenter.handleTangemInfo(tangemInfo)
    }

    override fun cancel() {
        // Implement logic if needed.
    }

    override fun successfullyCompleteOperation(tangemInfo: TangemInfo?) {
        val data = Intent()
        data.putExtra(Constant.Extra.EXTRA_TANGEM_INFO, tangemInfo)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun setupToolbar(toolbarColor: Int, upArrow: Int, upArrowColor: Int, titleColor: Int) {
        setActionBarBackground(toolbarColor)
        setActionBarIcon(upArrow, upArrowColor)
        setActionBarTitleColor(titleColor)
    }

    override fun showHelpScreen(articleId: Long) {
        SupportManager.showFreshdeskArticle(this, articleId)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}