package com.pickfords.surveyorapp.view

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.utils.Session
import com.pickfords.surveyorapp.view.dialogs.ProgressDialog
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.toolbar.tvTitle


open class BaseFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity
    private var progressDialog: ProgressDialog? = null
    lateinit var session: Session


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
        mActivity = context as Activity
        session = Session(mActivity)

    }

    fun setupToolbarWithMenu(title: String? = null) {
        (activity as AppCompatActivity).setSupportActionBar(requireActivity().toolbarDashboard)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menup)
            if (title != null) requireActivity().tvTitle.text = title
        }

    }
    fun showProgressbar(message: String? = requireActivity().getString(R.string.please_wait)) {
        hideProgressbar()
        if (progressDialog == null) {
            progressDialog = ProgressDialog(requireContext(), message)
        }
        progressDialog?.show()
    }

    fun hideProgressbar() {
        if (isAdded&&progressDialog != null && progressDialog?.isShowing!!) progressDialog!!.dismiss()
    }

    fun changeToInventoryPage(){

    }

}
