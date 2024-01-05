package com.pickfords.surveyorapp.view.surveyDetails

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import com.pickfords.surveyorapp.DashboardActivity
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.databinding.FragmentCommentsBinding
import com.pickfords.surveyorapp.interfaces.FragmentLifecycleInterface
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.model.surveyDetails.CommentsDetailModel
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.BaseFragment
import com.pickfords.surveyorapp.view.dialogs.MessageDialog
import com.pickfords.surveyorapp.viewModel.surveyDetails.CommentsViewModel

class FragmentComments : BaseFragment(), View.OnClickListener, FragmentLifecycleInterface {

    private lateinit var commentsBinding: FragmentCommentsBinding
    private val viewModel by lazy { CommentsViewModel(requireActivity()) }
    private var selectedData: DashboardModel? = null
    var surveyId: Int? = null
    private var isFirstTime: Boolean = true
    private var selectedPosition: Int = 0

    companion object {
        fun newInstance(selectedData: DashboardModel?): FragmentComments {
            val bundle = Bundle()
            bundle.putSerializable(Session.DATA, selectedData)
            val fragmentComments = FragmentComments()
            fragmentComments.arguments = bundle
            return fragmentComments
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        commentsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_comments,
            container,
            false
        )
        setSpinnerTitle()
        setObserver()
        setAction()
        return commentsBinding.root
    }

    //  Set Tittle For Spinner
    private fun setSpinnerTitle() {
        commentsBinding.spnSequence.setTitle("Select Sequence")
    }

    // For All list Observer
    private fun setObserver() {
        viewModel.getSelectCommentsDetailModel().observe(requireActivity()) { comments ->
            if (comments != null)
                commentsBinding.data = comments
            else
                commentsBinding.data = CommentsDetailModel()
        }

        viewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        viewModel.selectedPosition.observe(requireActivity()) {
            selectedPosition = it
            commentsBinding.spnSequence.setSelection(selectedPosition)
            isFirstTime = false
        }

    }

    // Set All click here
    private fun setAction() {
        commentsBinding.btnClear.setOnClickListener {
            if (viewModel.getSelectSequenceModel().value!![selectedPosition].isRevisit) {
                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).show()
            } else {
                if (!TextUtils.isEmpty(commentsBinding.evPfReason.text.toString()) ||
                    !TextUtils.isEmpty(commentsBinding.evCustomerResponse.text.toString()) ||
                    !TextUtils.isEmpty(commentsBinding.evGeneral.text.toString()) ||
                    !TextUtils.isEmpty(commentsBinding.evOps.text.toString()) ||
                    !TextUtils.isEmpty(commentsBinding.evOpsConf.text.toString())
                ) {
                    dialog()
                }
            }
        }


        clearError(commentsBinding.evPfReason)
        clearError(commentsBinding.evCustomerResponse)
        clearError(commentsBinding.evGeneral)
        clearError(commentsBinding.evOps)
        clearError(commentsBinding.evOpsConf)

        commentsBinding.btnSave.setOnClickListener {
            if (viewModel.getSelectSequenceModel().value != null && viewModel.getSelectSequenceModel().value?.size!! > 0 && viewModel.getSelectSequenceModel().value!![selectedPosition].isRevisit) {
                MessageDialog(mContext, mContext.getString(R.string.revisit_error)).setListener(
                    object : MessageDialog.OkButtonListener{
                        override fun onOkPressed(dialog: MessageDialog) {
                            dialog.dismiss()
                            resetValue()
                        }
                    }
                ).show()
            } else {
                val data = commentsBinding.data ?: CommentsDetailModel()
                data.pFReason = commentsBinding.evPfReason.text.toString()
                data.customerResp = commentsBinding.evCustomerResponse.text.toString()
                data.general = commentsBinding.evGeneral.text.toString()
                data.ops = commentsBinding.evOps.text.toString()
                data.opsConf = commentsBinding.evOpsConf.text.toString()
                data.createdBy = session.user!!.userId!!.toInt()
                data.surveyId = selectedData!!.surveyId.toInt()
                data.sequenceId =
                    viewModel.getSelectSequenceModel().value!![commentsBinding.spnSequence.selectedItemPosition].surveySequenceId
                viewModel.saveSurveyComment(data)
            }

        }
    }


    private fun dialog() {
        val builder = AlertDialog.Builder(requireActivity())
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setTitle("Alert")

        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want to clear the comment?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                commentsBinding.evPfReason.text?.clear()
                commentsBinding.evCustomerResponse.text?.clear()
                commentsBinding.evGeneral.text?.clear()
                commentsBinding.evOps.text?.clear()
                commentsBinding.evOpsConf.text?.clear()
            }

            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()

            }
        //Creating dialog box
        val alert: AlertDialog = builder.create()
        alert.show()

    }

    private fun clearError(editText: AppCompatEditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(editText.text.toString())) {
                    editText.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    override fun onClick(v: View?) {
    }

    override fun onPauseFragment() {
    }

    private fun setDefaultSequence() {
        try {
            viewModel.getSelectSequenceModel().observeForever {
                if (it != null) {
                    commentsBinding.spnSequence.setSelection(0)
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
    }


    override fun onResumeFragment(s: String?) {
        viewModel.init(mContext)
        commentsBinding.lifecycleOwner = this
        commentsBinding.viewModel = viewModel
        if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                Session.DATA
            ) != null
        ) {
            selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
            commentsBinding.mainData = selectedData
            surveyId = selectedData!!.surveyId.toInt()
            viewModel.setSurveyID(surveyId!!)
        }

        if (!isFirstTime) {
            viewModel.getSequenceList(selectedData?.surveyId.toString(), mContext)
            commentsBinding.spnSequence.post {
                commentsBinding.spnSequence.setSelection(selectedPosition)
            }
        } else {
            viewModel.getSequenceList(selectedData?.surveyId.toString(), mContext)
            commentsBinding.spnSequence.post {
                commentsBinding.spnSequence.setSelection(selectedPosition)
            }
        }

        // For select sequence code
        if((context as DashboardActivity).setSelectedSequencePosition() > 0){
            var selectedPosition =  ((context as DashboardActivity).setSelectedSequencePosition() - 1)
            Log.e("SelectedComment",selectedPosition.toString())
            viewModel.selectedPosition.postValue(selectedPosition)
            commentsBinding.spnSequence.setSelection(selectedPosition)
        }

    }

    private fun resetValue() {
        try {
            viewModel.init(mContext)
            commentsBinding.lifecycleOwner = this
            commentsBinding.viewModel = viewModel
            if (arguments != null && requireArguments().containsKey(Session.DATA) && requireArguments().get(
                    Session.DATA
                ) != null
            ) {
                selectedData = requireArguments().getSerializable(Session.DATA) as DashboardModel?
                commentsBinding.mainData = selectedData
                surveyId = selectedData!!.surveyId.toInt()
                viewModel.setSurveyID(surveyId!!)
            }

        }catch (e: Exception){
        }
    }
}