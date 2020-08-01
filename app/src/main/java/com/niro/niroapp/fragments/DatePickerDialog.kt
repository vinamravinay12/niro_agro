package com.niro.niroapp.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.niro.niroapp.utils.DateChangeListener
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.NIroAppConstants
import java.util.*

class DatePickerDialog(private val dialogContext: Context, private val dateChangeListener : DateChangeListener, private val selectedDate: String?) : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        if(selectedDate != null) calendar.time = DateUtils.getDate(selectedDate,NIroAppConstants.POST_DATE_FORMAT)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(dialogContext, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year,month,dayOfMonth)
        dateChangeListener.onDateChanged(DateUtils.getDateString(calendar.time,NIroAppConstants.POST_DATE_FORMAT))
    }

}