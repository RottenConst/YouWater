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
import ru.iwater.youwater.vm.ProductListViewModel
import ru.iwater.youwater.databinding.FragmentBasketBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

/**
 * Фрагмент корзины
 */
class BasketFragment : BaseFragment() {

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
        val navController = NavHostFragment.findNavController(this)
        binding.lifecycleOwner = this
        viewModel.getBasket()

        binding.composeViewBasketScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
//                    BasketScreen(viewModel, navController)
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BasketFragment()
    }
}