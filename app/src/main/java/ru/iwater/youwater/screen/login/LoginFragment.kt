package ru.iwater.youwater.screen.login

import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.login_fragment.*
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.utils.PhoneTextFormatter
import ru.iwater.youwater.utils.PinCodeFormatter

class LoginFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_enter.isEnabled = false
        et_tel_num.addTextChangedListener(PhoneTextFormatter(et_tel_num, "+7(###) ### ##-##", btn_enter))
        et_pin_code.addTextChangedListener(PhoneTextFormatter(et_pin_code, "####", btn_enter_home))
        btn_enter.setOnClickListener {
            val tel = et_tel_num.text.toString()
            tv_info_code.text = "Код отправлен на номер: $tel"
            tv_enter_pin.visibility = View.VISIBLE
            tv_info_code.visibility = View.VISIBLE
            btn_enter_home.visibility = View.VISIBLE
            et_pin_code.visibility = view.visibility
            et_tel_num.visibility = View.GONE
            til_tel_num.visibility = View.GONE
            btn_enter.visibility = View.GONE
        }
        btn_enter_home.setOnClickListener {
            MainActivity.start(context)
        }
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}