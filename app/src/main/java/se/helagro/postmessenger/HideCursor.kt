package se.helagro.postmessenger

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.Timer
import java.util.TimerTask

class HideCursor(private val editText: EditText) : TextWatcher{
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        editText.isCursorVisible = !p0.isNullOrEmpty()
    }
    override fun afterTextChanged(p0: Editable?) {}
}