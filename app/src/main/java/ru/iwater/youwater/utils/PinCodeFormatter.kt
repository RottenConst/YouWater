package ru.iwater.youwater.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PinCodeFormatter(val codeinput: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        codeinput.removeTextChangedListener(this)
        var text = s.toString()
        text = text.replace("[^0-9]".toRegex(), "")

        if (text.length > 4) text = text.substring(0, 4)

        var newText = ""
        for (c in text.toCharArray()) newText += "$c "
        text = newText.trim { it <= ' ' }

        val substring = "_ _ _ _".substring(text.length)
        text += substring

        val length = text.replace("([ ]?[_])+$".toRegex(), "").length

        codeinput.setText(text)
        codeinput.setSelection(length)
        codeinput.addTextChangedListener(this)
    }
}