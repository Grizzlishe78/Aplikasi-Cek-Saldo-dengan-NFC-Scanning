package com.unsoed.appscanning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var imgNfc: ImageView
    private lateinit var tvNfcResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        imgNfc = view.findViewById(R.id.imgNfc)
        tvNfcResult = view.findViewById(R.id.tvNfcResult)
        return view
    }

    fun updateNfcResult(uid: String, saldo: Int) {
        tvNfcResult.text = "Kartu UID: $uid\nSaldo: Rp $saldo"
        imgNfc.setImageResource(R.drawable.bg_card)
    }
}
