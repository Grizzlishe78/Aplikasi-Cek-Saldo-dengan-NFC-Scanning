package com.unsoed.appscanning

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.unsoed.appscanning.data.AppDatabase
import com.unsoed.appscanning.data.Reminder
import com.unsoed.appscanning.util.NotificationUtils
import kotlinx.coroutines.launch
import java.util.*

class AddEditReminderActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnSave: Button
    private lateinit var btnPickTime: Button

    private var selectedTimeMillis: Long = 0
    private var selectedTimeText: String = ""
    private var reminderId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_reminder)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription) // tambahkan di XML
        etAmount = findViewById(R.id.etAmount)
        btnSave = findViewById(R.id.btnSave)
        btnPickTime = findViewById(R.id.btnPickTime)

        reminderId = intent.getIntExtra("reminder_id", -1).takeIf { it != -1 }

        btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, h, m ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                selectedTimeMillis = calendar.timeInMillis
                selectedTimeText = String.format("%02d:%02d", h, m)
                btnPickTime.text = selectedTimeText
            }, hour, minute, true).show()
        }

        btnSave.setOnClickListener { saveReminder() }
    }

    private fun saveReminder() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val amount = etAmount.text.toString().toIntOrNull() ?: 0
        val currentDate = Calendar.getInstance().time.toString() // contoh format tanggal

        if (title.isEmpty() || selectedTimeMillis == 0L) {
            return
        }

        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val reminder = Reminder(
                id = reminderId ?: 0,
                title = title,
                description = description,
                time = selectedTimeMillis,
                amount = amount,
                date = currentDate
            )

            if (reminderId == null) db.reminderDao().insert(reminder)
            else db.reminderDao().update(reminder)

            NotificationUtils.showNotification(
                context = this@AddEditReminderActivity,
                title = "Reminder: $title",
                message = "Pengingat untuk pukul $selectedTimeText"
            )

            finish()
        }
    }
}
