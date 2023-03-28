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
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentCatalogProductBinding
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.CatalogListViewModel
import javax.inject.Inject


/**
 * Фрагмент списка товаров определённой категории
 */
class CatalogProductFragment : Fragment(), AdapterProductList.OnProductItemClickListener {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: CatalogListViewModel by viewModels { factory }
//    val binding: FragmentCatalogProductBinding by lazy { FragmentCatalogProductBinding.inflate(
//        LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCatalogProductBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val navController = NavHostFragment.findNavController(this)
        val catalogId = CatalogProductFragmentArgs.fromBundle(requireArguments()).typeId
        val catalogTitle = CatalogProductFragmentArgs.fromBundle(requireArguments()).typeString

        viewModel.products.observe(this.viewLifecycleOwner) { products ->
            val productList = products.filter { it.category == catalogId }
            binding.composeViewCatalogProduct.apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.Default
                )
                setContent {
                    YourWaterTheme {
                        ProductByCategory(
                            productsList = productList,
                            categoryName = catalogTitle,
                            getAboutProduct = {
                                navController.navigate(
                                    CatalogProductFragmentDirections.actionCatalogProductFragmentToAboutProductFragment(
                                        it
                                    )
                                )
                            },
                            addProductInBasket = { viewModel.addProductToBasket(it) },
                            addToFavorite = {viewModel.addToFavorite(it)},
                            deleteFavorite = {viewModel.deleteFavorite(it)}
                        )
                    }
                }
            }
        }

        return binding.root
    }

    override fun onProductItemClicked(product: Product) {
//        viewModel.addProductInBasket(product.id)
//        if (product.category != 20) {
//        } else {
//            Toast.makeText(this.context, "Стартовый пакет возможно заказать только 1", Toast.LENGTH_LONG).show()
//        }
    }

    override fun aboutProductClick(product: Product) {
        viewModel.displayProduct(product.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CatalogFragment()
    }
}