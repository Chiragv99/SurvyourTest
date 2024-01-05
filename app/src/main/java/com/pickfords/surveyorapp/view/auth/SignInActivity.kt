package com.pickfords.surveyorapp.view.auth

import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.ActivitySigninBinding
import com.pickfords.surveyorapp.extentions.goToActivityAndClearTask
import com.pickfords.surveyorapp.extentions.showToast
import com.pickfords.surveyorapp.model.auth.SignInModel
import com.pickfords.surveyorapp.utils.AsteriskPasswordTransformationMethod
import com.pickfords.surveyorapp.utils.EditTextErrorResolver
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseActivity
import com.pickfords.surveyorapp.viewModel.auth.SignInViewModel

class SignInActivity : BaseActivity() {
    private val signInViewModel by lazy { SignInViewModel(this) }
    lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)

        binding.viewModel = signInViewModel
        binding.lifecycleOwner = this
        
        setAction()
        setObserver()
    }

    private fun setObserver() {
        binding.edtPassword.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))

        binding.edtPassword.addTextChangedListener(
            EditTextErrorResolver(
                binding.edtPassword,
                binding.inpPwd
            )
        )
        binding.edtPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        signInViewModel.getSignInModel().observe(this) { user ->
            if (validate(user))
                signInViewModel.getUser()
        }

        signInViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showProgressbar()
            else hideProgressbar()
        }
        signInViewModel.userLiveData.observe(
            this,
        ) { user ->
            if (user != null) {
//             setSyncWorker() //todo offline support
                if (binding.chkRememberPassword.isChecked) {
                    session.storeDataByKey(
                        Session.KEY_USER_EMAIL,
                        binding.edtEmail.text.toString(),
                    )
                    session.storeDataByKey(
                        Session.KEY_USER_PASSWORD,
                        binding.edtPassword.text.toString(),
                    )
                    session.storeDataByKey(
                        Session.KEY_USER_REMEMBER,
                        binding.chkRememberPassword.isChecked,
                    )

                    session.storeDataByKey(Session.KEY_USER_NAME,user.firstName.toString() + " "+user.surname.toString())
                } else {
                    session.storeDataByKey(Session.KEY_USER_EMAIL, "")
                    session.storeDataByKey(Session.KEY_USER_PASSWORD, "")
                    session.storeDataByKey(Session.KEY_USER_REMEMBER, false)
                    session.storeDataByKey(Session.KEY_USER_NAME,"")
                }
                showToast("Hello ${user.firstName}")
                session.user = user
                goToActivityAndClearTask<DashboardActivity>()
            }
        }
        val email: String = session.getDataByKey(Session.KEY_USER_EMAIL, "")
        val pwd: String = session.getDataByKey(Session.KEY_USER_PASSWORD, "")
        session.storeDataByKey(Session.KEY_USER_NAME,"")
        signInViewModel.email.set(email)
        signInViewModel.password.set(pwd)

        binding.chkRememberPassword.isChecked = session.getDataByKey(Session.KEY_USER_REMEMBER, false)

        //Todo: Remove below static credentials on live and client build -Viraj
        //Live
//        signInViewModel.email.set("paul.turtill@pickfords.com")
//        signInViewModel.password.set("Paul@123")
        //staging
//        signInViewModel.email.set("atul.m@sgit.in")
//        signInViewModel.password.set("Sit@123")
    }

    private fun setAction() {

        binding.imgLoginHeader?.let {
            Glide.with(this)
                .load(R.drawable.login_bg_top)
                .centerCrop()
                .into(it)
        }

        binding.edtEmail.addTextChangedListener(
            EditTextErrorResolver(
                binding.edtEmail,
                binding.inpEmail
            )
        )
    }

    private fun validate(user: SignInModel): Boolean {
        var isValid = true
        if (TextUtils.isEmpty(user.email)) {
            isValid = false
            binding.inpEmail.error = getString(R.string.enter_email_address)
            binding.edtEmail.requestFocus()
        } else if (!user.isEmailValid()) {
            isValid = false
            binding.inpEmail.error = getString(R.string.enter_valid_email_address)
            binding.edtEmail.requestFocus()
        }
        if (TextUtils.isEmpty(user.password)) {
            isValid = false
            binding.inpPwd.error = getString(R.string.enter_a_password)
            binding.edtPassword.requestFocus()
        }
        else if (!user.isPasswordValid(3)){
            isValid = false
            binding.inpPwd.error = getString(R.string.below_eigth_characters)
            binding.edtPassword.requestFocus()
        }
        return isValid
    }
}
