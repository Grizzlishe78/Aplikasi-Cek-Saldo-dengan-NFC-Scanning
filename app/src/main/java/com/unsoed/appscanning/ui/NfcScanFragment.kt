package com.unsoed.appscanning.ui

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.unsoed.appscanning.R
import java.util.*

class NfcScanFragment : Fragment() {

    private lateinit var ivNfc: ImageView
    private lateinit var tvScanHint: TextView
    private lateinit var btnOpenReminders: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nfc_scan, container, false)
        ivNfc = view.findViewById(R.id.ivNfc)
        tvScanHint = view.findViewById(R.id.tvScanHint)
        btnOpenReminders = view.findViewById(R.id.btnOpenReminders)

        btnOpenReminders.setOnClickListener {
            // buka ReminderActivity (atau fragment daftar reminder jika Anda pakai fragment)
            startActivity(Intent(requireContext(), com.unsoed.appscanning.ReminderActivity::class.java))
        }
        return view
    }

    /**
     * Dipanggil oleh Activity ketika onNewIntent menerima Tag NFC.
     * Pastikan MainActivity meneruskan Tag ke sini.
     */
    fun onTagDiscovered(tag: Tag) {
        // format UID byte -> hex (masked)
        val uid = tag.id.joinToString(":") { b ->
            String.format(Locale.getDefault(), "%02X", b.toInt() and 0xFF)
        }

        val dummySaldo = (10000..500000).random()

        // tampilkan ResultFragment dengan data
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, com.unsoed.appscanning.ui.ResultFragment.newInstance(uid, dummySaldo))
            .addToBackStack(null)
            .commit()
    }
}
