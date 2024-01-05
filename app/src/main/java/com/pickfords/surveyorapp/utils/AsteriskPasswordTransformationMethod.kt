package com.pickfords.surveyorapp.utils

import android.text.method.PasswordTransformationMethod
import android.view.View

class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }

    inner class PasswordCharSequence(private val mSource: CharSequence) :
        CharSequence {

        override val length: Int
            get() = mSource.length // Return default

        override fun get(index: Int): Char {
            return '*' // This is the important part
        }

        override fun subSequence(start: Int, end: Int): CharSequence {
            return mSource.subSequence(start, end) // Return default
        }
    }
}