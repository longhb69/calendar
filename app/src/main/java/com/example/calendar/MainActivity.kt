package com.example.calendar

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener


class MainActivity : AppCompatActivity() {

    private lateinit var dateTV: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var addButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dateTV = findViewById(R.id.idTVDate)
        calendarView = findViewById(R.id.calendarView)
        addButton = findViewById(R.id.button_add_event)

        val currentDate = getCurrentDate()
        dateTV.text = currentDate

        calendarView
            .setOnDateChangeListener(
                OnDateChangeListener { view, year, month, dayOfMonth ->
                    val Date = (dayOfMonth.toString() + "-"
                            + (month + 1) + "-" + year)

                    dateTV.setText(Date)
                })

        addButton.setOnClickListener {
            val selectedDate = dateTV.text.toString()
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putExtra("date", selectedDate)
            startActivity(intent)
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

}