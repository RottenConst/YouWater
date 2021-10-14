package ru.iwater.youwater.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.AuthViewModel
import ru.iwater.youwater.data.StatusPhone
import ru.iwater.youwater.data.StatusPinCode
import ru.iwater.youwater.databinding.LoginFragmentBinding
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.utils.PhoneTextFormatter
import timber.log.Timber
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
            etPinCode.addTextChangedListener(PhoneTextFormatter(etPinCode, "####", btnEnterHome))
            btnEnter.setOnClickListener {
                val tel = etTelNum.text.toString()
                viewModel.authPhone(tel)
                "Код отправлен на номер: $tel".also { tvInfoCode.text = it }
                viewModel.statusPhone.observe(viewLifecycleOwner, { status ->
                    when(status) {
                        StatusPhone.LOAD -> {

                        }
                        StatusPhone.DONE -> {
                            tvEnterPin.visibility = View.VISIBLE
                            tvInfoCode.visibility = View.VISIBLE
                            btnEnterHome.visibility = View.VISIBLE
                            etPinCode.visibility = View.VISIBLE
                            etPinCode.isSelected = true
                            etTelNum.visibility = View.GONE
                            tilTelNum.visibility = View.GONE
                            btnEnter.visibility = View.GONE
                        }
                        StatusPhone.ERROR -> {
                            Timber.d("EROOR")
                        }
                    }
                })
            }
            btnEnterHome.setOnClickListener {
                viewModel.checkPin(context, etPinCode.text.toString())
                viewModel.statusPinCode.observe(viewLifecycleOwner, { status->
                    when (status) {
                        StatusPinCode.DONE -> {
                            Toast.makeText(context, "Успешно", Toast.LENGTH_LONG).show()
                        }
                        StatusPinCode.ERROR -> {
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                        }
                        StatusPinCode.LOAD -> {

                        }
                    }
                })

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