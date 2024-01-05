package com.pickfords.surveyorapp.utils

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.pickfords.surveyorapp.R
import com.pickfords.surveyorapp.model.dashboard.DashboardModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("imageUrl")
fun loadImageGenericImage(view: ImageView, url: String) {
    Glide.with(view)
        .load(url)
        .apply(RequestOptions().placeholder(R.drawable.placeholder))
        .into(view)
}

@BindingAdapter("setImageCenterInside")
fun loadImageCenterInside(view: ImageView, url: String) {
    Glide.with(view)
        .load(url)
        .apply(RequestOptions().centerInside())
        .into(view)


}

@BindingAdapter("setLinearVerticalAdapter")
fun bindRecyclerViewAdapter(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<*>?
) {
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter
}

@BindingAdapter("setLinearHorizontalAdapter")
fun bindRecyclerViewHorizontalAdapter(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<*>?
) {
    recyclerView.layoutManager =
        LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
    recyclerView.adapter = adapter
}

@BindingAdapter("setDashboardGridAdapter")
fun bindRecyclerGridAdapter(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<*>?
) {
    recyclerView.layoutManager =
        GridLayoutManager(recyclerView.context, 2)
    recyclerView.adapter = adapter
}

@BindingAdapter("setInventoryGridAdapter")
fun bindRecyclerInventoryGridAdapter(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<*>?
) {
    recyclerView.layoutManager =
        GridLayoutManager(recyclerView.context, 5)
    recyclerView.adapter = adapter
}

@BindingAdapter(value = ["setGridAdapter", "column"], requireAll = true)
fun bindRecyclerViewGridAdapter(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<*>?,
    spanCount: Int
) {
    recyclerView.layoutManager =
        GridLayoutManager(recyclerView.context, spanCount)
    recyclerView.adapter = adapter
}

@BindingAdapter(value = ["setPagerAdapter", "indicator"], requireAll = false)
fun bindBannerPagerAdapter(
    viewPager: ViewPager,
    adapter: PagerAdapter,
    indicator: DotsIndicator?
) {
    viewPager.adapter = adapter
    indicator?.setViewPager(viewPager)
}

@BindingAdapter("nestedScroll")
fun nestedScrollEnable(view: NestedScrollView, bool: Boolean) {
    view.isNestedScrollingEnabled = bool
}

@BindingAdapter("timestamp")
fun setTimestamp(view: TextView, value: Long) {
    val cal = Calendar.getInstance()
    cal.timeInMillis = value * 1000L
    val output = SimpleDateFormat("hh:mm a, dd MMM yyyy")
    try {
        view.text = output.format(cal.timeInMillis)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}

@BindingAdapter("convertDateTime")
fun convertDateTime(view: TextView, value: String?) {
    if (value != null) {
        val cal = Calendar.getInstance()
        try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            cal.time = input.parse(value)
            val output = SimpleDateFormat("dd/MM/yyyy")
            view.text = output.format(cal.timeInMillis)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}


fun convertDateTimeReturn(value: String?) : String {
    if (value != null) {
        val cal = Calendar.getInstance()
        try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            cal.time = input.parse(value)
            val output = SimpleDateFormat("dd/MM/yyyy")
            return output.format(cal.timeInMillis)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return ""
}

@BindingAdapter("convertDateTimeDashboard")
fun convertDateTimeDashboard(view: TextView, value: String?) {
    if (value != null) {
        val cal = Calendar.getInstance()
        try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            cal.time = input.parse(value)
            val output = SimpleDateFormat("dd/MM/yyyy hh:mm a")
            view.text = output.format(cal.timeInMillis)
            Log.e("SurveyDate", view.text.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}


@BindingAdapter("updateTimeFromDate")
fun updateTimeFromDate(view: TextView, value: String?) {
    if (value != null) {
        val cal = Calendar.getInstance()
        try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            cal.time = input.parse(value)
            val output = SimpleDateFormat("HH:mm")
            view.text = output.format(cal.timeInMillis)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}

@BindingAdapter("updateTimeFromDateDashboard")
fun updateTimeFromDateDashboard(view: TextView, value: String?) {
/*    if (value != null){
        val cal = Calendar.getInstance()
        try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss",Locale.US)
            cal.time = input.parse(value)
            val output = SimpleDateFormat("hh:mm aa",Locale.US)
            view.text = output.format(cal.timeInMillis)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }*/
    val dt: Date
    if (value != null)
        try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            dt = input.parse(value)
            val output = SimpleDateFormat("HH:mm")
            view.text = output.format(dt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
}


@BindingAdapter("convertDateTimeSurvey")
fun convertDateTimeSurvey(view: TextView, value: String?) {
    val cal = Calendar.getInstance()
    try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        cal.time = input.parse(value)
        val output = SimpleDateFormat("dd/MM/yyyy")
        view.text = "Survey Date : " + output.format(cal.timeInMillis)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}

@BindingAdapter("convertSurveyTime")
fun convertSurveyTime(view: TextView, value: String?) {
    val cal = Calendar.getInstance()
    if (value != null) {
        try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            cal!!.time = input.parse(value!!)
            val output = SimpleDateFormat("hh:mm a")
            view.text = "" + output.format(cal.timeInMillis)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}

@BindingAdapter("convertDateTimeSet")
fun convertDateTimeSet(view: TextInputEditText, value: String?) {
    if (value != null && value.isNotEmpty()) {

        val cal = Calendar.getInstance()
        val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        cal.time = input.parse(value)
        val output = SimpleDateFormat("dd/MMM/yyyy")
        try {
            view.setText(output.format(cal.timeInMillis))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}

@BindingAdapter("convertEnqiryTime")
fun convertEnqiryTime(view: TextView, value: DashboardModel?) {
    val cal = Calendar.getInstance()
    val calDate = Calendar.getInstance()
    try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        if (value?.surveyDate != null) {
            calDate.time = input.parse(value?.surveyDate)
        }
        if (value?.surveyTime != null) {
            cal.time = input.parse(value?.surveyTime)
        }

        val output = SimpleDateFormat("HH:mm")
        val outputDate = SimpleDateFormat("dd/MMM/yyyy")
        val date = "" + outputDate.format(calDate.timeInMillis)
        val time = " " + output.format(cal.timeInMillis)
        view.setText(date + time)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}

@BindingAdapter("convertDateTimeSet")
fun convertDateTimeSet(view: AppCompatTextView, value: String?) {
    if (value != null && value.isNotEmpty()) {

        val cal = Calendar.getInstance()
        val input = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        cal.time = input.parse(value)
        val output = SimpleDateFormat("MMM/dd/yyyy")
        try {
            view.text = output.format(cal.timeInMillis)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}

@BindingAdapter("setAdapterPosition")
fun setAdapterPosition(spinner: AppCompatSpinner, result: ObservableField<String>) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            result.set(parent!!.getItemAtPosition(position) as String)
        }
    }
}