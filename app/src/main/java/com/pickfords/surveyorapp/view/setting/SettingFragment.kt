package com.pickfords.surveyorapp.view.setting

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentSettingBinding
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.viewModel.auth.SettingViewModel
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : BaseFragment() {

    lateinit var settingBinding: FragmentSettingBinding
    private val viewModel by lazy { SettingViewModel(requireActivity()) }
    var selectedData: DashboardModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_setting,
            container,
            false
        )
        return settingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithMenu(requireActivity().getString(R.string.change_password))
        if (savedInstanceState == null)
            viewModel.init(mContext)

        if (arguments != null && requireArguments().containsKey(Session.DATA)) {
            selectedData = requireArguments()[Session.DATA] as DashboardModel?
        }

        settingBinding.lifecycleOwner = this
        settingBinding.viewModel = viewModel
        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
        settingBinding.btSavePassword.setOnClickListener {
            when {
                TextUtils.isEmpty(settingBinding.edCurrentPwd.text.toString()) -> {
                    settingBinding.edCurrentPwd.error =
                        requireActivity().getString(R.string.current_password_validation)
                    settingBinding.edCurrentPwd.requestFocus()
                }
                TextUtils.isEmpty(settingBinding.edNewPwd.text.toString()) -> {
                    settingBinding.edNewPwd.error =
                        requireActivity().getString(R.string.new_password_validation)
                    settingBinding.edNewPwd.requestFocus()
                }
                TextUtils.isEmpty(settingBinding.edConfirmPwd.text.toString()) -> {
                    settingBinding.edConfirmPwd.error =
                        requireActivity().getString(R.string.confirm_new_password_validation)
                    settingBinding.edConfirmPwd.requestFocus()

                }
                settingBinding.edNewPwd.text.toString()
                    .trim() != settingBinding.edConfirmPwd.text.toString().trim() -> {
                    settingBinding.edNewPwd.requestFocus()
                    settingBinding.edNewPwd.error = context?.getString(R.string.password_validation)
                }
                else -> {
                    viewModel.savePassword(
                        mContext,
                        session.user!!.userId?.trim().toString(),
                        settingBinding.edCurrentPwd.text.toString().trim(),
                        settingBinding.edNewPwd.text.toString().trim()
                    )
                }
            }
        }
        viewModel.isClearField.observeForever {
            if (it) {
                edCurrentPwd.setText("")
                edNewPwd.setText("")
                edConfirmPwd.setText("")
            }
        }
    }
}