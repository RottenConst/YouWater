package ru.iwater.youwater.screen.catalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentCatalogProductBinding
import ru.iwater.youwater.vm.ProductListViewModel
import ru.iwater.youwater.screen.adapters.AdapterProductList
import javax.inject.Inject


/**
 * Фрагмент списка товаров определённой категории
 */
class CatalogProductFragment : Fragment(), AdapterProductList.OnProductItemClickListener {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    private val viewModel: ProductListViewModel by viewModels { factory }
    private val binding: FragmentCatalogProductBinding by lazy { FragmentCatalogProductBinding.inflate(
        LayoutInflater.from(this.context)) }
    private val adapterProductList: AdapterProductList by lazy { getAdapterProduct() }

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
        viewModel.addProductInBasket(product.id)
        if (product.category != 20) {
            getMessage("Товар ${product.app_name} добавлен в корзину")
                .setAction("Корзина") {
                    findNavController().navigate(CatalogProductFragmentDirections.actionCatalogProductFragmentToBasketFragment())
                }.show()
        } else {
            Toast.makeText(this.context, "Стартовый пакет добавлен", Toast.LENGTH_LONG).show()
        }
    }

    override fun aboutProductClick(product: Product) {
        viewModel.displayProduct(product.id)
    }

    private fun getAdapterProduct(): AdapterProductList {
        return AdapterProductList(AdapterProductList.OnClickListener {
            if (!it.onFavoriteClick) {
                viewModel.viewModelScope.launch {
                    if (!viewModel.deleteFavoriteProduct(it)) {
                        getMessage("Ошибка").show()
                    }
                }
            } else {
                viewModel.viewModelScope.launch {
                    if (viewModel.addProductInFavorite(it)) {
                        getMessage("Товар добавлен в избранное")
                            .setAction("Избранное") {
                                findNavController()
                                    .navigate(CatalogProductFragmentDirections.actionCatalogProductFragmentToFavoriteFragment())
                            }.show()
                    } else getMessage("Ошибка").show()
                }
            }
        }, this)
    }

    private fun getMessage(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)

    companion object {
        @JvmStatic
        fun newInstance() =
            CatalogFragment()
    }
}