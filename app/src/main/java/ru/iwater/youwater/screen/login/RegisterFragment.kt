package ru.iwater.youwater.screen.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.iwater.youwater.base.App
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.data.StatusPhone
import ru.iwater.youwater.databinding.FragmentRegisterBinding
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
        binding.btnRegister.setOnClickListener {
            val name = binding.etNameRegister.text.toString()
            val email = binding.etEmailRegister.text.toString()
            when {
                name.isNullOrEmpty() -> {
                    Toast.makeText(context, "Введите ваше имя", Toast.LENGTH_LONG).show()
                }
                email.isNullOrEmpty() -> {
                    Toast.makeText(context, "Введите адрес электронной почты", Toast.LENGTH_LONG).show()
                }
                else -> {
                    viewModel.registerClient(phoneNumber, name, email)
                }
            }
            viewModel.statusPhone.observe(viewLifecycleOwner) { status ->
                when (status) {
                    StatusPhone.LOAD -> {

                    }
                    StatusPhone.DONE -> {
                        viewModel.clientId.observe(viewLifecycleOwner) { id ->
                            if (id != null) {
                                this.findNavController().navigate(
                                    RegisterFragmentDirections.actionRegisterFragmentToEnterPinCodeFragment(
                                        phoneNumber, id
                                    )
                                )
                            } else {
                                Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                    StatusPhone.ERROR -> {
                        Toast.makeText(context, "Ошибка", Toast.LENGTH_LONG).show()
                    }
                    StatusPhone.NET_ERROR -> {
                        Toast.makeText(context, "ошибка соединения", Toast.LENGTH_LONG).show()
                    }
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