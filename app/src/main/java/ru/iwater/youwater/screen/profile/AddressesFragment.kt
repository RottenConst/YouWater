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
import ru.iwater.youwater.databinding.FragmentAddresessBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

/**
 * фрагмент выводит список активных адресов клиента
 */
class AddressesFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ClientProfileViewModel by viewModels { factory }

    private val screenComponent = App().buildScreenComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddresessBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val navController = NavHostFragment.findNavController(this)
        binding.composeViewAddressesScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    AddressesScreen(clientProfileViewModel = viewModel,navController = navController)
                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAddressesList()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddressesFragment()
    }
}