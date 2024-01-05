package com.pickfords.surveyorapp.view.auth

import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ActivityForgotPasswordBinding
import com.pickfords.surveyorapp.extentions.goToActivityAndClearTask
import com.pickfords.surveyorapp.extentions.showToast
import com.pickfords.surveyorapp.utils.EditTextErrorResolver
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.viewModel.auth.ForgotPasswordViewModel

class ForgotPasswordActivity : BaseActivity() {
    private val forgotPasswordViewModel by lazy { ForgotPasswordViewModel(this) }
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        binding.viewModel = forgotPasswordViewModel
        binding.lifecycleOwner = this
        setAction()
        observer()

    }


    // Set All Observer Here
    private fun observer() {
        forgotPasswordViewModel.getSignInModel().observe(this) { user ->
            if (TextUtils.isEmpty(user.email)) {
                binding.inpEmail.error = getString(R.string.enter_email_address)
                binding.edtEmail.requestFocus()
            } else if (!user.isEmailValid()) {
                binding.inpEmail.error = getString(R.string.enter_valid_email_address)
                binding.edtEmail.requestFocus()
            } else {
                forgotPasswordViewModel.getUser()
            }
        }
        forgotPasswordViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showProgressbar()
            else hideProgressbar()
        }
        forgotPasswordViewModel.userLiveData.observe(this) { user ->
            if (user != null) {
                showToast("Hello ${user.firstName}")
                goToActivityAndClearTask<DashboardActivity>()
            }
        }
    }
 // All click Listener Here
    private fun setAction() {
        binding.edtEmail.addTextChangedListener(
            EditTextErrorResolver(
                binding.edtEmail,
                binding.inpEmail
            )
        )
    }
}
