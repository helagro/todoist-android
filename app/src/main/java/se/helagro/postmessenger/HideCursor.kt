package se.helagro.postmessenger

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.Timer
import java.util.TimerTask

class HideCursor(val editText: EditText) : TextWatcher{
    private var timer: Timer? = null

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {
        editText.isCursorVisible = true
        resetTimer()
    }

    private fun resetTimer() {
        timer?.cancel()

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                editText.isCursorVisible = false
            }
        }, 10000)
    }
}