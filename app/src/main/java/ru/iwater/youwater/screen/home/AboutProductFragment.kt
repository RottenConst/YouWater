package ru.iwater.youwater.screen.home

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
import ru.iwater.youwater.vm.AboutProductViewModel
import ru.iwater.youwater.databinding.FragmentAboutProductBinding
import ru.iwater.youwater.theme.YourWaterTheme
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
        val navController = NavHostFragment.findNavController(this)
        binding.lifecycleOwner = this
        binding.composeViewAboutProductScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent { 
                YourWaterTheme {
//                    AboutProductScreen(aboutProductViewModel = viewModel, productId =  productId, navController = navController)
                }
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