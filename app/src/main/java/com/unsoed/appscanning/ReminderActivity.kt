package com.unsoed.appscanning

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unsoed.appscanning.data.AppDatabase
import com.unsoed.appscanning.data.Reminder
import com.unsoed.appscanning.notification.ReminderReceiver
import kotlinx.coroutines.launch
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReminderAdapter
    private val reminders = mutableListOf<Reminder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReminderAdapter(
            reminders,
            onItemClick = { reminder ->
                val intent = Intent(this, AddEditReminderActivity::class.java)
                intent.putExtra("reminder_id", reminder.id)
                startActivity(intent)
            },
            onDeleteClick = { reminder ->
                confirmDelete(reminder)
            }
        )
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddEditReminderActivity::class.java))
        }

        requestPermissionsIfNeeded()
    }

    // Cek izin notifikasi dan alarm
    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    this,
                    "Aktifkan izin 'Alarm' agar pengingat berjalan tepat waktu.",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    private fun loadReminders() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val data = db.reminderDao().getAllReminders()
            runOnUiThread {
                reminders.clear()
                reminders.addAll(data)
                adapter.notifyDataSetChanged()

                if (reminders.isEmpty()) {
                    Toast.makeText(
                        this@ReminderActivity,
                        "Belum ada reminder",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    reminders.forEach { scheduleReminder(it) }
                }
            }
        }
    }

    private fun scheduleReminder(reminder: Reminder) {
        if (reminder.time < System.currentTimeMillis()) return

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("message", reminder.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.time,
                        pendingIntent
                    )
                }
                else -> {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminder.time,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Gagal menjadwalkan alarm: perlu izin Alarm.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Konfirmasi hapus
    private fun confirmDelete(reminder: Reminder) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Reminder")
            .setMessage("Apakah Anda yakin ingin menghapus reminder \"${reminder.title}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteReminder(reminder)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteReminder(reminder: Reminder) {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            db.reminderDao().delete(reminder)
            cancelReminder(reminder)

            runOnUiThread {
                adapter.removeItem(reminder)
                Toast.makeText(this@ReminderActivity, "Reminder dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelReminder(reminder: Reminder) {
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 &&
            (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
        ) {
            Toast.makeText(this, "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
        }
    }
}
