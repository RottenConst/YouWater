package ru.iwater.youwater.screen.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.vm.AuthViewModel
import ru.iwater.youwater.databinding.FragmentEnterPinCodeBinding
import ru.iwater.youwater.theme.YourWaterTheme
import javax.inject.Inject

class EnterPinCodeFragment : Fragment() {

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
        val binding = FragmentEnterPinCodeBinding.inflate(inflater)
        val phoneNumber = EnterPinCodeFragmentArgs.fromBundle(requireArguments()).phoneNumber
        val clientId = EnterPinCodeFragmentArgs.fromBundle(requireArguments()).clientId

        binding.composeViewEnterPinCode.apply {
             setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                YourWaterTheme {
                    EnterPinCodeScreen(phone = phoneNumber, clientId, this.context, viewModel, this@EnterPinCodeFragment.requireActivity())
                }
            }
        }
        return binding.root
    }

    companion object {
        fun newInstance() =
            EnterPinCodeFragment()
    }
}