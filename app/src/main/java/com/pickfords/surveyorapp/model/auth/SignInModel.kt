package com.pickfords.surveyorapp.model.auth

import android.util.Patterns

class SignInModel {
    var email: String? = null
    var password: String? = null

    fun isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()
    }

    fun isPasswordValid(length: Int): Boolean {
        return password.toString().length >= length
    }
}