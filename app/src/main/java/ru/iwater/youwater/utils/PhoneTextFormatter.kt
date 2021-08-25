package ru.iwater.youwater.utils

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import timber.log.Timber


class PhoneTextFormatter(
    private val mEditText: EditText,
    private val mPattern: String,
    private val mButton: Button
) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val phone = StringBuilder(s)
        Timber.d("join")
        if (count > 0 && !isValid(phone.toString())) {
            for (i in 0 until phone.length) {
                Timber.d(String.format("%s", phone))
                val c = mPattern[i]
                if (c != '#' && c != phone[i]) {
                    phone.insert(i, c)
                }
            }
            mEditText.setText(phone)
            mEditText.setSelection(mEditText.text.length)
        }
        mButton.isEnabled = mEditText.text.length == mPattern.length
    }

    override fun afterTextChanged(s: Editable) {}
    private fun isValid(phone: String): Boolean {
        for (i in phone.indices) {
            val c = mPattern[i]
            if (c == '#') continue
            if (c != phone[i]) {
                return false
            }
        }
        return true
    }

    init {
        //set max length of string
        val maxLength = mPattern.length
        mButton.isEnabled = false
        mEditText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
    }
}