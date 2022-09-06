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
import ru.iwater.youwater.vm.AboutProductViewModel
import ru.iwater.youwater.databinding.FragmentAboutProductBinding
import javax.inject.Inject

/**
 * Фрагмент подробной информации о товаре
 */
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
        val productId = AboutProductFragmentArgs.fromBundle(requireArguments()).orderId
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.initProduct(productId)
        viewModel.product.observe(this.viewLifecycleOwner) { product ->
            if (product != null) {
                //клик по "+"
                binding.btnPlusCount.setOnClickListener {
                    viewModel.plusCountProduct(product)
                }

                //клик по "-"
                binding.btnMinusCount.setOnClickListener {
                    viewModel.minusCountProduct(product)
                }

                //клик по кнопке "добавить в корзину"
                binding.btnBuyProduct.setOnClickListener {
                    viewModel.addProductToBasket(product)
                    if (product.category != 20) { //является ли товар стартовым пакетом
                        Snackbar.make(
                            binding.constraintAboutProduct,
                            "Товар ${product.app_name} добавлен в корзину",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Перейти в корзину") {
                                this.findNavController()
                                    .navigate(AboutProductFragmentDirections.actionAboutProductFragmentToBasketFragment())
                            }.show()
                    } else {
                        Toast.makeText(
                            this.context,
                            "Стартовый пакет возможно заказать только 1",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                //клик по "?"
                binding.ibGetPrice.setOnClickListener {
                    viewModel.displayPrice(product.price)
                    viewModel.navigateToPriceProduct.observe(this.viewLifecycleOwner) {
                        if (it != null) {
                            this.findNavController()
                                .navigate(
                                    AboutProductFragmentDirections.actionAboutProductFragmentToPriceBottomSheetFragment(
                                        it
                                    )
                                )
                            viewModel.displayPriceComplete()
                        }

                    }

                }
            } else {
                Toast.makeText(this.context, "Неудается загрузить информацию о заказе", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AboutProductFragment()
    }
}