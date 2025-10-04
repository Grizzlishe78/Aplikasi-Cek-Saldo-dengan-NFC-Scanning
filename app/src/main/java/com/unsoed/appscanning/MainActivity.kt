package com.unsoed.appscanning

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.room.Room
import com.unsoed.appscanning.data.AppDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var btnOpenReminders: Button

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var homeFragment: HomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "reminder_db"
        ).build()

        btnOpenReminders = findViewById(R.id.btnOpenReminders)

        btnOpenReminders.setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }

        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, homeFragment!!)
            }
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC tidak tersedia di perangkat ini", Toast.LENGTH_LONG).show()
        } else {
            pendingIntent = PendingIntent.getActivity(
                this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        tag?.let {
            val uid = it.id.joinToString(":") { byte -> "%02X".format(byte) }
            val dummySaldo = (10000..500000).random()

            homeFragment?.updateNfcResult(uid, dummySaldo)
        }
    }
}
