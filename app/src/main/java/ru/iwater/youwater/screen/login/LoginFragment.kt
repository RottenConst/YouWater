package ru.iwater.youwater.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.data.StatusPhone
import ru.iwater.youwater.data.StatusPinCode
import ru.iwater.youwater.databinding.LoginFragmentBinding
import ru.iwater.youwater.utils.PhoneTextFormatter
import javax.inject.Inject

/**
 * Фрагмент авторизации пользователя (ввод телефона\пин кода)
 */
class LoginFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    private val viewModel: AuthViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LoginFragmentBinding.inflate(inflater)
        binding.btnEnter.isEnabled = false
        binding.apply {
            etTelNum.addTextChangedListener(
                PhoneTextFormatter(
                    etTelNum,
                    "+7(###) ###-####",
                    btnEnter
                )
            )
            btnEnter.setOnClickListener {
                val tel = etTelNum.text.toString()
                viewModel.authPhone(tel)
                viewModel.statusPhone.observe(viewLifecycleOwner) { status ->
                    when (status) {
                        StatusPhone.LOAD -> {

                        }
                        StatusPhone.DONE -> {
                            viewModel.clientId.observe(viewLifecycleOwner) { id ->
                                if (id != null) {
                                    it.findNavController().navigate(
                                        LoginFragmentDirections.actionLoginFragmentToEnterPinCodeFragment(
                                            tel, id
                                        )
                                    )
                                } else {
                                    Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        StatusPhone.ERROR -> {
                            it.findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(tel)
                            )
                        }
                        StatusPhone.NET_ERROR -> {
                            Toast.makeText(context, "ошибка соединения", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        return binding.root
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}