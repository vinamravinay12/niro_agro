package com.niro.niroapp.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.niro.niroapp.utils.DateChangeListener
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.NiroAppConstants
import java.util.*

class DatePickerDialog(private val dialogContext: Context, private val dateChangeListener : DateChangeListener, private val selectedDate: String?,private val minDate : Date? = null) : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        if(selectedDate != null) calendar.time = DateUtils.getDate(selectedDate,NiroAppConstants.POST_DATE_FORMAT)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog =  DatePickerDialog(dialogContext, this, year, month, day)
        if(minDate != null) datePickerDialog.datePicker.minDate = minDate.time

        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year,month,dayOfMonth)
        dateChangeListener.onDateChanged(DateUtils.getDateString(calendar.time,NiroAppConstants.POST_DATE_FORMAT))
    }

}