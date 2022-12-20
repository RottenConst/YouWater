package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.FavoriteProduct
import ru.iwater.youwater.databinding.FragmentFavoriteBinding
import ru.iwater.youwater.screen.adapters.FavoriteProductAdapter
import ru.iwater.youwater.vm.CatalogListViewModel
import javax.inject.Inject

class FavoriteFragment : BaseFragment(), FavoriteProductAdapter.OnFavoriteProductClickListener {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val screenComponent = App().buildScreenComponent()
    val viewModel: CatalogListViewModel by viewModels { factory }
    val binding: FragmentFavoriteBinding by lazy { FragmentFavoriteBinding.inflate(LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        val adapter = FavoriteProductAdapter(this)
        binding.rvFavoriteProduct.adapter = adapter
        viewModel.getFavoriteProduct()
        viewModel.favoriteProducts.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.tvNothingFavorite.visibility = View.VISIBLE
            } else {
                binding.tvNothingFavorite.visibility = View.GONE
            }
        }
        viewModel.navigateToSelectProduct.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    FavoriteFragmentDirections.actionFavoriteFragmentToAboutProductFragment(it)
                )
                viewModel.displayProductComplete()
            }
        }
        return binding.root
    }

    override fun onLikeItemClick(favoriteProduct: FavoriteProduct) {
        viewModel.viewModelScope.launch {
            if (viewModel.delFavoriteProduct(favoriteProduct)) {
                getMessage("Товар удален из избранного")
                    .setAction("Отмена") {
                        viewModel.addFavoriteProduct(favoriteProduct, it)
                    }.show()
            } else {
                getMessage("Ошибка").show()
            }
        }
    }

    override fun addItemInBasked(favoriteProduct: FavoriteProduct) {
        viewModel.addProductInBasket(favoriteProduct.id)
        getMessage("Товар добавлен в корзину")
            .setAction("корзина") {
                this.findNavController()
                    .navigate(FavoriteFragmentDirections.actionFavoriteFragmentToBasketFragment())
            }.show()
    }

    override fun aboutFavoriteClick(favoriteProduct: FavoriteProduct) {
        viewModel.displayProduct(favoriteProduct.id)
    }

    private fun getMessage(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }
}