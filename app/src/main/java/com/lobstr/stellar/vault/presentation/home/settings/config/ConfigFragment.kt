package com.lobstr.stellar.vault.presentation.home.settings.config

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.config.Config
import com.lobstr.stellar.vault.presentation.home.settings.config.adapter.ConfigAdapter
import com.lobstr.stellar.vault.presentation.home.settings.config.adapter.OnConfigItemListener
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import kotlinx.android.synthetic.main.fragment_config.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class ConfigFragment : BaseFragment(), ConfigView, OnConfigItemListener {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ConfigFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    @InjectPresenter
    lateinit var mPresenter: ConfigPresenter

    private var mView: View? = null

    // ===========================================================
    // Constructors
    // ===========================================================

    @ProvidePresenter
    fun provideConfigPresenter() = ConfigPresenter(
        arguments?.getInt(Constant.Bundle.BUNDLE_CONFIG)!!
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
        mView = if (mView == null) inflater.inflate(
            R.layout.fragment_config,
            container,
            false
        ) else mView
        return mView
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    override fun setupToolbarTitle(title: String?) {
        saveActionBarTitle(title)
    }

    override fun setupConfigTitle(title: String?) {
        tvConfigTitle.text = title
    }

    override fun initListComponents(configs: List<Config>, selectedType: Byte) {
        rvConfig.layoutManager = LinearLayoutManager(context)
        rvConfig.adapter = ConfigAdapter(configs, selectedType, this)
    }

    override fun setSelectedType(selectedType: Byte) {
        activity?.setResult(Activity.RESULT_OK)
        (rvConfig.adapter as? ConfigAdapter)?.setSelectedType(selectedType)
    }

    override fun onConfigItemClick(config: Config, selectedType: Byte) {
        mPresenter.configItemClicked(config, selectedType)
    }

    override fun setupConfigDescription(description: String?) {
        tvConfigDescription.text = description
    }

    override fun finishScreen() {
        activity?.finish()
    }

    override fun showProgressDialog(show: Boolean) {
        ProgressManager.show(show, childFragmentManager)
    }

    override fun showErrorMessage(message: String) {
        if (message.isEmpty()) {
            return
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
