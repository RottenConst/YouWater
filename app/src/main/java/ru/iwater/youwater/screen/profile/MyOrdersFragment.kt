package ru.iwater.youwater.screen.profile

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
import ru.iwater.youwater.data.ClientProfileViewModel
import ru.iwater.youwater.databinding.FragmentMyOrdersBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

class MyOrdersFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val screenComponent = App().buildScreenComponent()
    val viewModel: ClientProfileViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyOrdersBinding.inflate(inflater)
        val navController = NavHostFragment.findNavController(this)
        binding.lifecycleOwner = this
        binding.composeViewMyOrdersScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    MyOrdersScreen(viewModel, navController)
                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrderCrm()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyOrdersFragment()
    }
}