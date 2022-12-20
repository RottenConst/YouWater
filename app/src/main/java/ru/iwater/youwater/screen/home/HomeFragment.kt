package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.vm.CatalogListViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.PromoBanner
import ru.iwater.youwater.data.StatusLoading
import ru.iwater.youwater.data.TypeProduct
import ru.iwater.youwater.databinding.FragmentHomeBinding
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.screen.adapters.CatalogWaterAdapter
import ru.iwater.youwater.screen.adapters.PromoBannerAdapter
import ru.iwater.youwater.utils.ExtendedFloatingActionButtonScrollListener
import javax.inject.Inject

/**
 * Фрагмент для домашнего экрана
 */
class HomeFragment : BaseFragment(), AdapterProductList.OnProductItemClickListener, PromoBannerAdapter.OnBannerItemClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val screenComponent = App().buildScreenComponent()
    private val viewModel: CatalogListViewModel by viewModels { factory }
    private val binding: FragmentHomeBinding by lazy { FragmentHomeBinding.inflate(LayoutInflater.from(this.context)) }
    private val adapterWatter: CatalogWaterAdapter by lazy { getCatalogWaterAdapter() }
    private val adapterPromo: PromoBannerAdapter = PromoBannerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.rvTypeProductList.adapter = adapterWatter
        binding.rvPromo.adapter = adapterPromo

        binding.rvTypeProductList.addOnScrollListener(ExtendedFloatingActionButtonScrollListener(binding.fabRepeatOrder))

        viewModel.lastOrder.observe(viewLifecycleOwner) { lastOrder ->
            if (lastOrder != null) {
                binding.fabRepeatOrder.setOnClickListener {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToCreateOrderFragment(false, lastOrder)
                    )
                }
            } else {
                binding.fabRepeatOrder.visibility = View.GONE
            }
        }

        viewModel.screenLoading.observe(viewLifecycleOwner) { status ->
            when (status) {
                StatusLoading.LOADING -> {
                    binding.rvPromo.visibility = View.GONE
                    binding.tvLabelPromo.visibility = View.GONE
                    binding.rvTypeProductList.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressBar.progress
                }
                StatusLoading.DONE -> {
                    binding.rvPromo.visibility = View.VISIBLE
                    binding.tvLabelPromo.visibility = View.VISIBLE
                    binding.rvTypeProductList.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
                else -> {Toast.makeText(this.context, "Error", Toast.LENGTH_LONG)}
            }
        }

        viewModel.promoBanners.observe(viewLifecycleOwner) { banners ->
            if (banners.isNotEmpty()) {
                adapterPromo.submitList(banners)
                lifecycleScope.launch(Dispatchers.Main) {
                    var itemBanner = 0
                    while (itemBanner <= banners.size) {
                        delay(5000)
                        if (itemBanner != banners.size) {
                            itemBanner++
                            binding.rvPromo.scrollToPosition(itemBanner)
                        } else {
                            itemBanner = 0
                            binding.rvPromo.scrollToPosition(itemBanner)
                        }
                    }
                }
            } else {
                binding.tvLabelPromo.visibility = View.GONE
                binding.rvPromo.visibility = View.GONE
            }
        }

        viewModel.catalogList.observe(viewLifecycleOwner) { catalogs ->
            if (catalogs.isEmpty()) {
                Toast.makeText(this.context, "Ошибка не удалось загрузить котегории товаров", Toast.LENGTH_LONG).show()
            }
        }

        val productCatalog = Observer<Map<TypeProduct, List<Product>>> {
            adapterWatter.submitList(it.toList())
        }

        viewModel.catalogProductMap.observeForever(productCatalog)

        viewModel.navigateToSelectProduct.observe(this.viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionShowAboutProductFragment(it)
                )
                viewModel.displayProductComplete()
            }
        }

        viewModel.navigateToSelectBanner.observe(this.viewLifecycleOwner) { banner ->
            if (banner != null) {
                this.findNavController().navigate(
                    HomeFragmentDirections
                        .actionHomeFragmentToBannerInfoBottomSheetFragment(banner.name, banner.description)
                )
                viewModel.displayPromoInfoComplete()
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteProductId()
    }

    override fun onProductItemClicked(product: Product) {
        viewModel.addProductInBasket(product.id)
        if (product.category != 20) {
            getMessage("Товар ${product.app_name} добавлен в корзину")
                .setAction("Корзина") {
                    this.findNavController()
                        .navigate(HomeFragmentDirections.actionHomeFragmentToBasketFragment())
                }.show()
        } else {
            Toast.makeText(this.context, "Стартовый пакет возможно заказать только 1", Toast.LENGTH_LONG).show()
        }
    }

    override fun aboutProductClick(product: Product) {
        viewModel.displayProduct(product.id)
    }

    override fun onBannerItemClicked(banner: PromoBanner) {
        viewModel.displayPromoInfo(banner)
    }

    private fun getCatalogWaterAdapter(): CatalogWaterAdapter {
        return CatalogWaterAdapter(CatalogWaterAdapter.OnClickListener{
            if (!it.onFavoriteClick) {
                viewModel.viewModelScope.launch {
                    if (viewModel.deleteFavoriteProduct(it)) {
                        getMessage("Товар удален из избранного").show()
                    } else getMessage("Ошибка").show()
                }
            } else {
                viewModel.viewModelScope.launch{
                    if (viewModel.addProductInFavorite(it)) {
                        getMessage("Товар добавлен в избранное")
                            .setAction("Избранное") {
                                findNavController()
                                    .navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
                        }.show()
                    } else getMessage("Ошибка").show()
                }
            }
        }, this)
    }

    private fun getMessage(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}