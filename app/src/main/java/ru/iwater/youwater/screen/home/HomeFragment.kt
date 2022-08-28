package ru.iwater.youwater.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.vm.CatalogListViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentHomeBinding
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.screen.adapters.CatalogWaterAdapter
import javax.inject.Inject

/**
 * Фрагмент для домашнего экрана
 */
class HomeFragment : BaseFragment(), AdapterProductList.OnProductItemClickListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val screenComponent = App().buildScreenComponent()
    private val viewModel: CatalogListViewModel by viewModels { factory }
    private val binding: FragmentHomeBinding by lazy { FragmentHomeBinding.inflate(LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.refreshProduct()
        val adapterWatter = CatalogWaterAdapter(CatalogWaterAdapter.OnClickListener{
            if (!it.onFavoriteClick) {
                viewModel.deleteFavoriteProduct(it)
            } else {
                viewModel.addProductInFavorite(it)
                Snackbar.make(binding.frameHome, "Товар ${it.app_name} добавлен в избранное", Snackbar.LENGTH_LONG)
                    .setAction("Избранное") {
                        this.findNavController()
                            .navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
                    }.show()

            }
        }, this)
        binding.rvTypeProductList.adapter = adapterWatter
        viewModel.catalogProductMap.observe(viewLifecycleOwner) { catalogs ->
            adapterWatter.submitList(catalogs.toList())
        }

        viewModel.navigateToSelectProduct.observe(this.viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionShowAboutProductFragment(it)
                )
                viewModel.displayProductComplete()
            }
        }
        return binding.root
    }

    override fun onProductItemClicked(product: Product) {
        viewModel.addProductInBasket(product)
        if (product.category != 20) {
            Snackbar.make(
                binding.frameHome,
                "Товар ${product.app_name} добавлен в корзину",
                Snackbar.LENGTH_LONG
            )
                .setAction("Перейти в корзину", View.OnClickListener {
                    this.findNavController()
                        .navigate(HomeFragmentDirections.actionHomeFragmentToBasketFragment())
                }).show()
        } else {
            Toast.makeText(this.context, "Стартовый пакет возможно заказать только 1", Toast.LENGTH_LONG).show()
        }
    }

    override fun aboutProductClick(product: Product) {
        viewModel.displayProduct(product.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}