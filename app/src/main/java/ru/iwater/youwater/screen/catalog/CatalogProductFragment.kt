package ru.iwater.youwater.screen.catalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentCatalogProductBinding
import ru.iwater.youwater.data.ProductListViewModel
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.screen.home.HomeFragmentDirections
import javax.inject.Inject


/**
 * Фрагмент списка товаров определённой категории
 */
class CatalogProductFragment : Fragment(), AdapterProductList.OnProductItemClickListener {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: ProductListViewModel by viewModels { factory }
    val binding: FragmentCatalogProductBinding by lazy { FragmentCatalogProductBinding.inflate(
        LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        val catalogId = CatalogProductFragmentArgs.fromBundle(requireArguments()).typeId
        val catalogTitle = CatalogProductFragmentArgs.fromBundle(requireArguments()).typeString
        val adapterProductList = AdapterProductList(AdapterProductList.OnClickListener {

        }, this)
        viewModel.setCatalogItem(catalogId)
        binding.tvLabelCatalog.text = catalogTitle
        binding.rvProductList.adapter = adapterProductList
        viewModel.productsList.observe(this.viewLifecycleOwner) {
            adapterProductList.submitList(it)
        }

        viewModel.navigateToSelectProduct.observe(this.viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    CatalogProductFragmentDirections.actionCatalogProductFragmentToAboutProductFragment(it)
                )
                viewModel.displayProductComplete()
            }
        }

        return binding.root
    }

    override fun onProductItemClicked(product: Product) {
        viewModel.addProductInBasket(product)
        Snackbar.make(binding.constraintCatalogProduct, "Товар ${product.app_name} добавлн в корзину", Snackbar.LENGTH_LONG)
            .setAction("Перейти в корзину") {
                this.findNavController().navigate(CatalogProductFragmentDirections.actionCatalogProductFragmentToBasketFragment())
            }.show()
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