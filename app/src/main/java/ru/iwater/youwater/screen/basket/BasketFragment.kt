package ru.iwater.youwater.screen.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.Product
import ru.iwater.youwater.vm.ProductListViewModel
import ru.iwater.youwater.databinding.FragmentBasketBinding
import ru.iwater.youwater.screen.adapters.AdapterBasketList
import timber.log.Timber
import javax.inject.Inject

/**
 * Фрагмент корзины
 */
class BasketFragment : BaseFragment(), AdapterBasketList.OnProductItemListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: ProductListViewModel by viewModels {factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
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
//        binding.btnCheckoutOrder.isEnabled = false
        viewModel.productsList.observe(viewLifecycleOwner) { products ->
            adapterBasketList.submitList(products)
            binding.btnCheckoutOrder.isEnabled = products.isNotEmpty() && products != null
            var priceTotal = 0
            var priceCompleteDiscount = 0
            var priceComplete = 0
            var priceDiscountTotal = 0
            var discount = 0
            var price = 0
            products.forEach { product ->
                val prices =
                    product.price.removeSuffix(";") //тут я убираю последнюю точку с запятой что б null'a не было
                val priceList = prices.split(";") //делю на массив по ;
                val count = product.count //количество товара узнаю
                if (product.id == 81 || product.id == 84) {
                    priceList.forEach {
                        val priceCount =
                            it.split(":") //дальше уже узнаю цены и сравниваю с количеством
                        if (priceCount[0].toInt() <= count) {
                            discount = (priceCount[1].toInt() - 15) * count
                            price = priceCount[1].toInt() * count
                        }
                    }
                    priceCompleteDiscount += discount
                } else {
                    priceList.forEach {
                        val priceCount =
                            it.split(":") //дальше уже узнаю цены и сравниваю с количеством
                        if (priceCount[0].toInt() <= count) {
                            price = priceCount[1].toInt() * count
                        }
                    }
                    priceComplete += price
                }
                priceTotal += price
                priceDiscountTotal = (priceComplete + priceCompleteDiscount)
                Timber.d("DISCOUNT $priceComplete $priceCompleteDiscount")
            }
            "${priceDiscountTotal}₽".also { binding.tvSumComplete.text = it }
            "${priceTotal}₽".also { binding.tvSumOrder.text = it }

        }
        binding.btnCheckoutOrder.setOnClickListener {
            this.findNavController().navigate(
                BasketFragmentDirections.actionBasketFragmentToCreateOrderFragment(false, 0)
            )
        }
        return binding.root
    }

    override fun deleteProductClick(product: Product) {
        viewModel.deleteProductFromBasket(product)
        viewModel.updateBasket()
    }

    override fun addProduct(product: Product) {
        viewModel.addProductInBasket(product.id)
        viewModel.updateBasket()
    }

    override fun minusProduct(product: Product) {
        viewModel.minusCountProduct(product)
        viewModel.updateBasket()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BasketFragment()
    }
}