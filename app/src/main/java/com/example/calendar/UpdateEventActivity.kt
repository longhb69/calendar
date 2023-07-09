package com.example.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.app.Activity
import android.view.View

class UpdateEventActivity : AppCompatActivity() {
    private lateinit var date: String
    private lateinit var eventEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var updateButton: Button


    private var originalEvent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        date = intent.getStringExtra("date") ?: ""
        originalEvent = intent.getStringExtra("event")


        eventEditText = findViewById(R.id.editText_event)
        saveButton = findViewById(R.id.button_save_event)
        updateButton = findViewById(R.id.button_update_event)

        supportActionBar?.title = date

        if (originalEvent != null) {
            eventEditText.setText(originalEvent)
            updateButton.visibility = View.VISIBLE
        }

        saveButton.setOnClickListener {
            val event = eventEditText.text.toString().trim()
            if (event.isNotEmpty()) {
                val returnIntent = Intent()
                returnIntent.putExtra("date", date)
                returnIntent.putExtra("newEvent", event)
                returnIntent.putExtra("oldEvent", originalEvent)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } else {
                Toast.makeText(this, "Please enter an event", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

