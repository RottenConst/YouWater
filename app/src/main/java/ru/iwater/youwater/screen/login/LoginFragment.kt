package ru.iwater.youwater.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.databinding.LoginFragmentBinding
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.utils.PhoneTextFormatter

/**
 * Фрагмент авторизации пользователя (ввод телефона\пин кода)
 */
class LoginFragment : BaseFragment() {

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
                    "+7(###) ### ##-##",
                    btnEnter
                )
            )
            etPinCode.addTextChangedListener(PhoneTextFormatter(etPinCode, "####", btnEnterHome))
            btnEnter.setOnClickListener {
                val tel = etTelNum.text.toString()
                "Код отправлен на номер: $tel".also { tvInfoCode.text = it }
                tvEnterPin.visibility = View.VISIBLE
                tvInfoCode.visibility = View.VISIBLE
                btnEnterHome.visibility = View.VISIBLE
                etPinCode.visibility = View.VISIBLE
                etTelNum.visibility = View.GONE
                tilTelNum.visibility = View.GONE
                btnEnter.visibility = View.GONE
            }
            btnEnterHome.setOnClickListener {
                MainActivity.start(context)
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