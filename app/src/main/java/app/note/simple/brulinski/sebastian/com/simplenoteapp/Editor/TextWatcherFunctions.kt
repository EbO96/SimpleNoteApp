package app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


class TextWatcherFunctions : TextWatcher {
    private var _beforeTextChanged: ((CharSequence, Int, Int, Int) -> Unit)? = null
    private var _onTextChanged: ((CharSequence, Int, Int, Int) -> Unit)? = null
    private var _afterTextChanged: ((Editable) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit
            = _beforeTextChanged?.invoke(s, start, count, after)!!

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit
            = _onTextChanged?.invoke(s, start, before, count)!!

    override fun afterTextChanged(s: Editable): Unit
            = _afterTextChanged?.invoke(s)!!

    fun beforeTextChanged(function: (CharSequence, Int, Int, Int) -> Unit) {
        _beforeTextChanged = function
    }

    fun onTextChanged(function: (CharSequence, Int, Int, Int) -> Unit) {
        _onTextChanged = function
    }

    fun afterTextChanged(function: (Editable) -> Unit) {
        _afterTextChanged = function
    }
}

fun EditText.addTextChangedListener(init: TextWatcherFunctions.() -> Unit): TextWatcher {
    val watcher = TextWatcherFunctions()
    watcher.init()
    addTextChangedListener(watcher)
    return watcher
}