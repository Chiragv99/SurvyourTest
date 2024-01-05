package com.pickfords.surveyorapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditTextErrorResolver : TextWatcher {
    private var editText: EditText? = null
    private var compatEditText: AppCompatEditText? = null
    private var inputEditText: TextInputEditText? = null
    private var textInputLayout: TextInputLayout

    constructor(editText: EditText?, textInputLayout: TextInputLayout) {
        this.editText = editText
        this.textInputLayout = textInputLayout
    }

    constructor(compatEditText: AppCompatEditText?, textInputLayout: TextInputLayout) {
        this.compatEditText = compatEditText
        this.textInputLayout = textInputLayout
    }

    constructor(inputEditText: TextInputEditText?, textInputLayout: TextInputLayout) {
        this.inputEditText = inputEditText
        this.textInputLayout = textInputLayout
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        if (editText != null) {
            if (editText!!.text.toString().isNotEmpty()) {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }
        }
        if (compatEditText != null) {
            if (compatEditText!!.text.toString().isNotEmpty()) {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }
        }
        if (inputEditText != null) {
            if (inputEditText!!.text.toString().isNotEmpty()) {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }
        }
    }

    override fun afterTextChanged(s: Editable) {}
}