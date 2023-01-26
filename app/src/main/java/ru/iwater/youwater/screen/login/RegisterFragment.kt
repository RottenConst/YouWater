package ru.iwater.youwater.screen.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.databinding.FragmentRegisterBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject


class RegisterFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    private val viewModel: AuthViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterBinding.inflate(inflater)
        val phoneNumber = RegisterFragmentArgs.fromBundle(requireArguments()).phoneNumber

        binding.composeViewRegister.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                YourWaterTheme {
                    RegisterScreen(viewModel, phoneNumber, this@RegisterFragment.findNavController())
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RegisterFragment()
    }
}