package com.example.calendar

import com.example.calendar.AddEventActivity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
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
import android.widget.LinearLayout
import android.util.Log
import android.util.TypedValue
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var dateTV: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var addButton: Button

    // HashMap to store events for each date
    private val eventMap: HashMap<String, MutableList<String>> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dateTV = findViewById(R.id.idTVDate)
        calendarView = findViewById(R.id.calendarView)
        addButton = findViewById(R.id.button_add_event)

        val currentDate = getCurrentDate()
        dateTV.text = currentDate

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            dateTV.text = selectedDate
            displayEvents(selectedDate)
        }

        addButton.setOnClickListener {
            val selectedDate = dateTV.text.toString()
            val selectedEvent = (it as TextView).text.toString()
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putExtra("date", selectedDate)
            startActivityForResult(intent, ADD_EVENT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EVENT_REQUEST_CODE && resultCode == RESULT_OK) {
            val date = data?.getStringExtra("date") ?: ""
            val event = data?.getStringExtra("event") ?: ""
            addEvent(date, event)
            displayEvents(date)
        }
        else if(requestCode == MODIFY_EVENT_REQUEST_CODE && resultCode == RESULT_OK) {
            val date = data?.getStringExtra("date") ?: ""
            val newEvent = data?.getStringExtra("newEvent") ?: ""
            val oldEvent = data?.getStringExtra("oldEvent") ?: ""
            updateEvent(date, oldEvent, newEvent)
            displayEvents(date)
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

    private fun displayEvents(date: String) {
        val events = eventMap[date]
        val eventContainer = findViewById<LinearLayout>(R.id.eventContainer)
        eventContainer.removeAllViews()

        events?.forEach { event ->
            val layout = RelativeLayout(this)
            val textView = TextView(this)
            textView.text = event
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

            val isNotDone = isUpdateEnabled(event)

            if (isNotDone) {
                textView.setTextColor(ContextCompat.getColor(this, R.color.b))
                textView.setTypeface(null, Typeface.BOLD)
            } else {
                textView.setTextColor(ContextCompat.getColor(this, R.color.gray))
                textView.setTypeface(null, Typeface.BOLD)
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            textView.setOnClickListener {
                val selectedEvent = (it as TextView).text.toString()
                val intent = Intent(this, UpdateEventActivity::class.java)
                intent.putExtra("date", date)
                intent.putExtra("event", selectedEvent)
                startActivityForResult(intent, MODIFY_EVENT_REQUEST_CODE)
            }

            if (isUpdateEnabled(event)) {
                val button = Button(this)
                button.text = "Done"
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.Jam))
                button.setTextColor(ContextCompat.getColor(this, R.color.white))

                button.setPadding(
                    resources.getDimensionPixelSize(R.dimen.button_padding_start), 10, resources.getDimensionPixelSize(R.dimen.button_padding_end), 10
                )

                button.setOnClickListener {
                    markEventAsDone(date, event)
                }

                val textViewParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                textViewParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                textViewParams.addRule(RelativeLayout.CENTER_VERTICAL)

                val buttonParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                buttonParams.addRule(RelativeLayout.CENTER_VERTICAL)
                buttonParams.width = resources.getDimensionPixelSize(R.dimen.button_width)
                buttonParams.height = resources.getDimensionPixelSize(R.dimen.button_height)
                buttonParams.marginEnd = resources.getDimensionPixelSize(R.dimen.margin_event_button)

                layout.addView(textView, textViewParams)
                layout.addView(button, buttonParams)
                layout.setPadding(0, 10, resources.getDimensionPixelSize(R.dimen.button_spacing), 10)
            }
            else {
                val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

                layout.addView(textView, layoutParams)
            }

            eventContainer.addView(layout)
        }
    }
    private fun addEvent(date: String, event: String) {
        val events = eventMap[date]
        if (events != null) {
            events.add(event)
        } else {
            eventMap[date] = mutableListOf(event)
        }
    }
    private fun updateEvent(date: String, oldEvent:String, newEvent:String) {
        if(eventMap.containsKey(date)) {
            val events = eventMap[date]
            val index = events?.indexOf(oldEvent)
            if(index != null && index != -1) {
               events[index] = newEvent
            }
        }
    }
    private fun markEventAsDone(date: String, event: String) {
        val events = eventMap[date]
        events?.let {
            val index = it.indexOf(event)
            if (index != -1) {
                val updatedEvent = "DONE $event"
                it[index] = updatedEvent
                Toast.makeText(this, "Event marked as done: $event", Toast.LENGTH_SHORT).show()
                displayEvents(date)
            }
        }
    }
    private fun isUpdateEnabled(event: String): Boolean {
        return !event.contains("DONE")
    }
    companion object {
        private const val ADD_EVENT_REQUEST_CODE = 1
        private const val MODIFY_EVENT_REQUEST_CODE = 2
    }
}
