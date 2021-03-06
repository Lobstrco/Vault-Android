package com.lobstr.stellar.vault.presentation.home.settings.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lobstr.stellar.vault.R
import com.lobstr.stellar.vault.databinding.FragmentConfigBinding
import com.lobstr.stellar.vault.presentation.base.fragment.BaseFragment
import com.lobstr.stellar.vault.presentation.entities.config.Config
import com.lobstr.stellar.vault.presentation.home.settings.config.adapter.ConfigAdapter
import com.lobstr.stellar.vault.presentation.util.Constant
import com.lobstr.stellar.vault.presentation.util.manager.ProgressManager
import com.lobstr.stellar.vault.presentation.util.manager.SupportManager
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class ConfigFragment : BaseFragment(), ConfigView {

    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        val LOG_TAG = ConfigFragment::class.simpleName
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<ConfigPresenter>

    // ===========================================================
    // Constructors
    // ===========================================================

    private val mPresenter by moxyPresenter {
        presenterProvider.get().apply {
            config = arguments?.getInt(Constant.Bundle.BUNDLE_CONFIG)!!
        }
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
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.config, menu)
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

    override fun setupToolbarTitle(title: String?) {
        saveActionBarTitle(title)
    }

    override fun setupConfigTitle(title: String?) {
        binding.tvConfigTitle.text = title
    }

    override fun initListComponents(configs: List<Config>, selectedType: Byte) {
        binding.rvConfig.layoutManager = LinearLayoutManager(context)
        binding.rvConfig.adapter = ConfigAdapter(configs, selectedType) { config, type ->
            mPresenter.configItemClicked(config, type)
        }
    }

    override fun setSelectedType(selectedType: Byte, config: Int) {
        activity?.setResult(Activity.RESULT_OK, Intent().apply { putExtra(Constant.Extra.EXTRA_CONFIG, config) })
        (binding.rvConfig.adapter as? ConfigAdapter)?.setSelectedType(selectedType)
    }

    override fun setupConfigDescription(description: String?) {
        binding.tvConfigDescription.text = description
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

    override fun showHelpScreen(articleId: Long, userId: String?) {
        if (articleId != -1L) {
            SupportManager.showZendeskArticle(requireContext(), articleId, userId)
        } else {
            SupportManager.showZendeskHelpCenter(requireContext())
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
