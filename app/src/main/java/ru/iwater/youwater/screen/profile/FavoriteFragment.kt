package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
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
        viewModel.delFavoriteProduct(favoriteProduct)
        viewModel.getFavoriteProduct()
        Snackbar.make( binding.root, "Товар удален из избранного", Snackbar.LENGTH_SHORT)
            .setAction("Отмена") {
                viewModel.addFavoriteProduct(favoriteProduct)
                binding.tvNothingFavorite.visibility = View.VISIBLE
                viewModel.getFavoriteProduct()
            }.show()
    }

    override fun addItemInBasked(favoriteProduct: FavoriteProduct) {
        viewModel.addProductInBasket(favoriteProduct.id)
        Snackbar.make(binding.root, "Товар добавлен в корзину", Snackbar.LENGTH_SHORT)
            .setAction("Перейти в корзину") {
                this.findNavController()
                    .navigate(FavoriteFragmentDirections.actionFavoriteFragmentToBasketFragment())
            }.show()
    }

    override fun aboutFavoriteClick(favoriteProduct: FavoriteProduct) {
        viewModel.displayProduct(favoriteProduct.id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }
}