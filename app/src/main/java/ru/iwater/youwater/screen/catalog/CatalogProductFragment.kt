package ru.iwater.youwater.screen.catalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.base.App
import ru.iwater.youwater.databinding.FragmentCatalogProductBinding
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.CatalogListViewModel
import javax.inject.Inject


/**
 * Фрагмент списка товаров определённой категории
 */
class CatalogProductFragment : Fragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: CatalogListViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getProductsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCatalogProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val navController = NavHostFragment.findNavController(this)
        val catalogId = CatalogProductFragmentArgs.fromBundle(requireArguments()).typeId

        binding.composeViewCatalogProduct.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    ProductByCategory(
                        catalogListViewModel = viewModel,
                        catalogId = catalogId,
                        navController = navController
                    )
                }
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CatalogFragment()
    }
}