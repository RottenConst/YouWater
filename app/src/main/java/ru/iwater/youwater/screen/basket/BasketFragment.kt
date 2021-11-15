package ru.iwater.youwater.screen.basket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.data.ProductListViewModel
import ru.iwater.youwater.databinding.FragmentBasketBinding
import ru.iwater.youwater.screen.adapters.AdapterBasketList
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Фрагмент корзины
 */
class BasketFragment : BaseFragment(), AdapterBasketList.OnProductItemListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: ProductListViewModel by viewModels {factory}

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val binding = FragmentBasketBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val adapterBasketList = AdapterBasketList(this)
        binding.rvBasketList.adapter = adapterBasketList
        viewModel.getBasket()
        viewModel.productsList.observe(viewLifecycleOwner, {
            adapterBasketList.submitList(it)
        })
        return binding.root
    }

    override fun deleteProductClick(product: Product) {
        viewModel.deleteProductFromBasket(product)
        viewModel.getBasket()
    }

    override fun addProduct(product: Product) {
        viewModel.addCountProduct(product)
        viewModel.getBasket()
    }

    override fun minusProduct(product: Product) {
        viewModel.minusCountProduct(product)
        viewModel.getBasket()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BasketFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}