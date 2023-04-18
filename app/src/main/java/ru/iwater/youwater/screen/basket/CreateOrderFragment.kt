package ru.iwater.youwater.screen.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentCreateOrderBinding
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.ProductListViewModel
import javax.inject.Inject

/**
 * Фрагмент оформления заказа
 */
class CreateOrderFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    val viewModel: ProductListViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // показывать сообщение о неудачной оплате:
        // true - да, false - нет
        val isShowMessage = CreateOrderFragmentArgs.fromBundle(this.requireArguments()).isShowMessage
//        val lastOrder = CreateOrderFragmentArgs.fromBundle(this.requireArguments()).lastOrderId
        val navController = NavHostFragment.findNavController(this)
        val binding = FragmentCreateOrderBinding.inflate(inflater)
        warningPay(isShowMessage)

        binding.lifecycleOwner = this
        /**
         * информация о клиенте
         */
        viewModel.getBasket()
        binding.composeViewCreateOrderScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    CreateOrderScreen(productListViewModel = viewModel, navController = navController, parentFragmentManager)
                }
            }
        }
        return binding.root
    }

    // отмена добавления коментария


    companion object {
        @JvmStatic
        fun newInstance() = CreateOrderFragment()
    }

    private fun warning() {
        Toast.makeText(this.context, "Извините оплатить заказ не удалось, попробуйте выбрать другой тип оплаты или отредактировать заказ", Toast.LENGTH_LONG).show()
    }

    private fun warningPay(isShowMessage: Boolean) {
        if (isShowMessage) {
            warning()
        }
    }
}