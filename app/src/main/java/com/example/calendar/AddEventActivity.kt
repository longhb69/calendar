package com.example.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddEventActivity: AppCompatActivity() {
    private lateinit var date: String
    private lateinit var eventEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        // Get the selected date from the intent
        date = intent.getStringExtra("date") ?: ""

        eventEditText = findViewById(R.id.editText_event)
        saveButton = findViewById(R.id.button_save_event)

        // Set the action bar title to the selected date
        supportActionBar?.title = date

        // Save button click listener
        saveButton.setOnClickListener {
            val event = eventEditText.text.toString().trim()
            if (event.isNotEmpty()) {
                // Perform saving logic here
                Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter an event", Toast.LENGTH_SHORT).show()
            }
        }
    }
}