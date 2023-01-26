package ru.iwater.youwater.screen.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.data.StatusPinCode
import ru.iwater.youwater.databinding.FragmentEnterPinCodeBinding
import ru.iwater.youwater.screen.StartActivity
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
                    EnterPincodeScreen(phone = phoneNumber, clientId, this@EnterPinCodeFragment.activity, viewModel)
                }
            }
        }
//        binding.tvInfoCode.text = "Код отправлен на номер: $phoneNumber"
//        binding.etPinCode.requestFocus()
//
//        binding.btnEnterPin.setOnClickListener {
//            viewModel.checkPin(context, binding.etPinCode.text.toString(), clientId)
//            viewModel.statusPinCode.observe(viewLifecycleOwner) { status ->
//                when (status) {
//                    StatusPinCode.DONE -> {
//                        Toast.makeText(context, "Успешно", Toast.LENGTH_LONG).show()
//                        this.activity?.finish()
//                    }
//                    StatusPinCode.ERROR -> {
//                        Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
//                    }
//                    StatusPinCode.LOAD -> {
//
//                    }
//                    else -> Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
        return binding.root
    }

    companion object {
        fun newInstance() =
            EnterPinCodeFragment()
    }
}