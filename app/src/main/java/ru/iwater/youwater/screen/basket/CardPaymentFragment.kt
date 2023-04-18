package ru.iwater.youwater.screen.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.FragmentCardPaymentBinding
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.ProductListViewModel
import javax.inject.Inject


class CardPaymentFragment : BaseFragment() {

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
        val binding = FragmentCardPaymentBinding.inflate(inflater)
        val formUrl = CardPaymentFragmentArgs.fromBundle(this.requireArguments()).formUrl
        val orderId = CardPaymentFragmentArgs.fromBundle(this.requireArguments()).orderId
        val navController = NavHostFragment.findNavController(this)
        binding.composeViewPaymentCardScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    LoadUrl(productListViewModel = viewModel, orderId = orderId, url = formUrl, navController = navController)
                }
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = CardPaymentFragment()
    }
}