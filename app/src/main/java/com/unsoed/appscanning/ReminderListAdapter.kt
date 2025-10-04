package com.unsoed.appscanning

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.unsoed.appscanning.data.Reminder

class ReminderListAdapter(
    private val context: Context,
    private val reminders: MutableList<Reminder>
) : BaseAdapter() {

    override fun getCount(): Int = reminders.size
    override fun getItem(position: Int): Any = reminders[position]
    override fun getItemId(position: Int): Long = reminders[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_reminder, parent, false)

        val reminder = reminders[position]
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvAmount = view.findViewById<TextView?>(R.id.tvAmount) // optional

        tvTitle.text = reminder.title
        tvDate.text = reminder.date
        tvAmount?.text = "Rp ${reminder.amount}"

        return view
    }
}
