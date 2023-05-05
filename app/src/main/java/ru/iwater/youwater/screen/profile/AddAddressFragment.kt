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
import ru.iwater.youwater.databinding.FragmentAddAddressBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

/**
 * Фрагмент для добавления нового адреса клиента
 */
class AddAddressFragment : BaseFragment() {

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
        val binding = FragmentAddAddressBinding.inflate(inflater)
        val navController = NavHostFragment.findNavController(this)
        binding.lifecycleOwner = this

        //true - перешли в добавление адреса из создание заявки, false - из меню адреса
        val isFromOrder = AddAddressFragmentArgs.fromBundle(this.requireArguments()).isFromOrder

        binding.composeViewAddAddressScreen.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.Default
            )
            setContent {
                YourWaterTheme {
                    AddAddressScreen(clientProfileViewModel = viewModel,navController = navController, isFromOrder = isFromOrder)
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddAddressFragment()
    }
}