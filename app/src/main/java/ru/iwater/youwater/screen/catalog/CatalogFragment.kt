package ru.iwater.youwater.screen.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentCatalogBinding
import ru.iwater.youwater.vm.CatalogListViewModel
import ru.iwater.youwater.screen.adapters.AdapterCatalogList
import javax.inject.Inject

/**
 * Фрагмент кататегорий товаров
 */
class CatalogFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    private val viewModel: CatalogListViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCatalogBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.rvCatalogList.adapter = AdapterCatalogList(AdapterCatalogList.OnClickListener {
            viewModel.displayCatalogList(it)
        })

        viewModel.navigateToSelectCategory.observe(this.viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    CatalogFragmentDirections.actionShowTypeCatalog(it.id, it.category)
                )
                viewModel.displayCatalogListComplete()
            }
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = CatalogFragment()
    }
}