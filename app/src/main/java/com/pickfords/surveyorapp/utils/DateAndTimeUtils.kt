package com.pickfords.surveyorapp.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*


class DateAndTimeUtils {

    companion object {

        @SuppressLint("SimpleDateFormat")
        fun setDate(context: Context, editText: EditText) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context, { _, yearCalendar, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, yearCalendar)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    // Display Selected date in textBox
                    editText.setText(SimpleDateFormat("dd/MMM/yyyy").format(calendar.time))

                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            if (datePickerDialog !=null){
                if (!datePickerDialog.isShowing){
                    datePickerDialog.show()
                }
            }
            val positiveColor =
                ContextCompat.getColor(context, android.R.color.holo_orange_dark)
            val negativeColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)

        }
        fun setDateWithformat(context: Context, editText: EditText,dateFormat: String) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context, { _, yearCalendar, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, yearCalendar)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    // Display Selected date in textBox
                    editText.setText(SimpleDateFormat(dateFormat).format(calendar.time))

                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            if (datePickerDialog !=null){
              if (!datePickerDialog.isShowing){
                  datePickerDialog.show()
              }
            }
            val positiveColor =
                ContextCompat.getColor(context, android.R.color.holo_orange_dark)
            val negativeColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)

        }

    }

}