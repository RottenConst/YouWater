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
import ru.iwater.youwater.data.CatalogListViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentHomeBinding
import ru.iwater.youwater.screen.adapters.AdapterProductList
import ru.iwater.youwater.screen.adapters.CatalogWaterAdapter
import ru.iwater.youwater.screen.basket.BasketFragment
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
        val adapterWatter = CatalogWaterAdapter(CatalogWaterAdapter.OnClickListener{
            if (!it.onFavoriteClick) {
                viewModel.deleteFavoriteProduct(it)
            } else {
                viewModel.addProductInFavorite(it)
                Snackbar.make(binding.frameHome, "Товар ${it.app_name} добавлен в избранное", Snackbar.LENGTH_LONG)
                    .setAction("Избранное", View.OnClickListener {
                        this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
                    }).show()
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

        viewModel.productLiveData.observe(viewLifecycleOwner) {
        }
        return binding.root
    }

    override fun onProductItemClicked(product: Product) {
        viewModel.addProductInBasket(product)
        Snackbar.make(binding.frameHome, "Товар ${product.app_name} добавлен в корзину", Snackbar.LENGTH_LONG)
            .setAction("Перейти в корзину", View.OnClickListener {
                this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBasketFragment())
        }).show()

    }

    override fun aboutProductClick(product: Product) {
        viewModel.displayProduct(product.id)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}