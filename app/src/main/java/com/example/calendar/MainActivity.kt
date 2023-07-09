package com.example.calendar

import com.example.calendar.AddEventActivity
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
import android.widget.LinearLayout
import android.util.Log
import android.widget.Toast


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
            val textView = TextView(this)
            textView.text = event
            textView.setOnClickListener {
                val selectedEvent = (it as TextView).text.toString()
                val intent = Intent(this, UpdateEventActivity::class.java)
                intent.putExtra("date", date)
                intent.putExtra("event", selectedEvent)
                startActivityForResult(intent, MODIFY_EVENT_REQUEST_CODE)
            }
            if (date == getCurrentDate()) {
                textView.setBackgroundResource(R.drawable.rounded_background_current_day) // Set rounded background for current day
            }

            if (isUpdateEnabled(event)) {
                val button = Button(this)
                button.text = "Mark as Done"
                button.setOnClickListener {
                    markEventAsDone(date, event)
                }

                val layout = LinearLayout(this)
                layout.orientation = LinearLayout.HORIZONTAL
                layout.addView(textView)
                layout.addView(button)

                eventContainer.addView(layout)
            } else {
                eventContainer.addView(textView)
            }

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
        // Modify the event status or remove it from the eventMap
        // based on your specific requirements
        val events = eventMap[date]
        events?.let {
            if (it.contains(event)) {
                it.remove(event)
                // Additional logic for marking event as done
                Toast.makeText(this, "Event marked as done: $event", Toast.LENGTH_SHORT).show()
                displayEvents(date)
            }
        }
    }
    private fun isUpdateEnabled(event: String): Boolean {
        // Customize the condition based on your requirements
        // Return true if the event can be updated, false otherwise
        // For example, you can check if the event is not marked as done
        return !event.startsWith("[DONE]")
    }
    companion object {
        private const val ADD_EVENT_REQUEST_CODE = 1
        private const val MODIFY_EVENT_REQUEST_CODE = 2
    }
}
