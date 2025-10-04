package com.unsoed.appscanning.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.unsoed.appscanning.R

class ResultFragment : Fragment() {

    companion object {
        private const val ARG_UID = "arg_uid"
        private const val ARG_SALDO = "arg_saldo"

        fun newInstance(uid: String, saldo: Int): ResultFragment {
            val f = ResultFragment()
            val args = Bundle()
            args.putString(ARG_UID, uid)
            args.putInt(ARG_SALDO, saldo)
            f.arguments = args
            return f
        }
    }

    private var uid: String? = null
    private var saldo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(ARG_UID)
            saldo = it.getInt(ARG_SALDO, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_result, container, false)
        val tvUid = view.findViewById<TextView>(R.id.tvUid)
        val tvSaldo = view.findViewById<TextView>(R.id.tvSaldo)
        val btnBack = view.findViewById<Button>(R.id.btnBackToScan)

        tvUid.text = "UID: ${uid ?: "-"}"
        tvSaldo.text = "Saldo: Rp ${String.format("%,d", saldo)}"

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }
}
