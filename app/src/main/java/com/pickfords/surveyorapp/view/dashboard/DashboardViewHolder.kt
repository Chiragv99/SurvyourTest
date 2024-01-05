package com.pickfords.surveyorapp.view.dashboard

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.pickfords.surveyorapp.databinding.ItemDashboardBinding
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.pickfords.surveyorapp.viewModel.dashboard.DashboardViewModel


class DashboardViewHolder(val binding: ItemDashboardBinding, val viewModel: DashboardViewModel) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: DashboardModel) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
        binding.viewmodel = viewModel
        if (data.isSubmitted || data.isCompletedSurvey == true) {
            binding.submitIcon.visibility = View.VISIBLE
        } else {
            binding.submitIcon.visibility = View.GONE
        }
    }

    fun onClickPhone(view: View, position: Int, data: DashboardModel?) {
        if (data?.phoneNo != null && data.phoneNo!!.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", data.phoneNo, null))
            view.context.startActivity(intent)
        } else {
            Toast.makeText(view.context, "Phone no not found", Toast.LENGTH_LONG).show()
        }
    }

    fun onClickEmail(view: View, position: Int, data: DashboardModel?) {
        if (data?.emailAddressB != null && data.emailAddressB!!.isNotEmpty()) {

            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL,"mailto:" + data.emailAddressB)
            try {
                view.context.startActivity(i)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    view.context,
                    "There are no email clients installed.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + data.emailAddressB))
            if (intent.resolveActivity(view.context.packageManager) != null) {
                view.context.startActivity(intent)
            }
        } else {
            Toast.makeText(view.context, "Email not found", Toast.LENGTH_LONG).show()
        }

    }

    fun onDelete(view: View, position: Int, data: DashboardModel) {
        viewModel.deleteSurvey(view.context, data.surveyId, data)
    }

    fun onClickCopy(view: View, position: Int, data: DashboardModel) {
        viewModel.copySurvey(view.context, data.surveyId)
    }
}