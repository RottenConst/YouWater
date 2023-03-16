package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.vm.CatalogListViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.PromoBanner
import ru.iwater.youwater.databinding.FragmentHomeBinding
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.screen.adapters.PromoBannerAdapter
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

/**
 * Фрагмент для домашнего экрана
 */
class HomeFragment : BaseFragment(), AdapterProductList.OnProductItemClickListener, PromoBannerAdapter.OnBannerItemClickListener {

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
        val binding = FragmentHomeBinding.inflate(inflater)
        val navController = NavHostFragment.findNavController(this)
        binding.composeViewHomeScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    HomeScreen(catalogListViewModel = viewModel, navController)
                }
            }
        }
        return binding.root
    }

    override fun onProductItemClicked(product: Product) {
        viewModel.addProductInBasket(product.id)
    }

    override fun aboutProductClick(product: Product) {
        viewModel.displayProduct(product.id)
    }

    override fun onBannerItemClicked(banner: PromoBanner) {
        viewModel.displayPromoInfo(banner)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}