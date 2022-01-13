package ru.iwater.youwater.screen.profile

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
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.FavoriteProduct
import ru.iwater.youwater.data.FavoriteViewModel
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.databinding.FragmentFavoriteBinding
import ru.iwater.youwater.screen.adapters.FavoriteProductAdapter
import ru.iwater.youwater.screen.home.HomeFragmentDirections
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : BaseFragment(), FavoriteProductAdapter.OnFavoriteProductClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val screenComponent = App().buildScreenComponent()
    val viewModel: FavoriteViewModel by viewModels { factory }
    val binding: FragmentFavoriteBinding by lazy { FragmentFavoriteBinding.inflate(LayoutInflater.from(this.context)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        val adapter = FavoriteProductAdapter(this)
        binding.rvFavoriteProduct.adapter = adapter
        viewModel.favoriteList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.tvNothingFavorite.visibility = View.VISIBLE
            } else {
                binding.tvNothingFavorite.visibility = View.GONE
            }
        })
        viewModel.navigateToSelectFavorite.observe(viewLifecycleOwner, { if (null != it) {
            this.findNavController().navigate(
                FavoriteFragmentDirections.actionFavoriteFragmentToAboutProductFragment(it)
            )
            viewModel.displayFavoriteComplete()
            }
        })
        return binding.root
    }

    override fun onLikeItemClick(favoriteProduct: Product) {
        viewModel.deleteFavoriteProduct(favoriteProduct)
        Snackbar.make(binding.constraintFavorite, "Товар ${favoriteProduct.app_name} удален", Snackbar.LENGTH_LONG)
            .setAction("Отмена", View.OnClickListener {
                viewModel.saveFavoriteProduct(favoriteProduct)
            }).show()
    }

    override fun addItemInBasked(favoriteProduct: Product) {
        viewModel.addFavoriteProductInBasket(favoriteProduct)
        Snackbar.make(binding.constraintFavorite, "Товар ${favoriteProduct.app_name} добавлен в корзину", Snackbar.LENGTH_LONG)
            .setAction("Перейти в корзину", View.OnClickListener {
                this.findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToBasketFragment())
            }).show()
    }

    override fun aboutFavoriteClick(favoriteProduct: Product) {
        viewModel.displayFavorite(favoriteProduct.id)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}