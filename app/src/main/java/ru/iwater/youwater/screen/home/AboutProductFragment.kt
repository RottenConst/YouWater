package ru.iwater.youwater.screen.home

import android.graphics.Paint
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
import ru.iwater.youwater.data.AboutProductViewModel
import ru.iwater.youwater.databinding.FragmentAboutProductBinding
import ru.iwater.youwater.screen.bindCostProduct
import javax.inject.Inject

class AboutProductFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: AboutProductViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutProductBinding.inflate(inflater)
        val productId = AboutProductFragmentArgs.fromBundle(arguments!!).orderId
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.initProduct(productId)
        binding.btnPlusCount.setOnClickListener {
            viewModel.plusCountProduct()
        }
        binding.btnMinusCount.setOnClickListener {
            viewModel.minusCountProduct()
        }
//        viewModel.product.observe(this.viewLifecycleOwner, { product ->
//            if (product != null) {
//                if (product.category == 1) {
//                    if (product.price.isNotEmpty()) {
//                        val price = product.price.split(";")[0].split(":")[1]
//                        "от ${price.toInt() - 15}₽".also { binding.tvPriceDiscount.text = it }
//                    }
//                } else {
//                    val price = product.price.split(";")[0].split(":")[1]
//                    binding.tvPriceDiscount.text = "от ${price}₽"
//                }
//            }
//        })
        binding.btnBuyProduct.setOnClickListener {
            viewModel.product.observe(this.viewLifecycleOwner, {
                viewModel.addProductToBasket(it)
                Snackbar.make(binding.constraintAboutProduct, "Товар ${it.app_name} добавлн в корзину", Snackbar.LENGTH_LONG)
                    .setAction("Перейти в корзину") {
                        this.findNavController()
                            .navigate(AboutProductFragmentDirections.actionAboutProductFragmentToBasketFragment())
                    }.show()
            })
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AboutProductFragment()
    }
}