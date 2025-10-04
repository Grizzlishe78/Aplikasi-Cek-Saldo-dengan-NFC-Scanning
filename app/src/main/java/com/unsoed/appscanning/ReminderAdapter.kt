package com.unsoed.appscanning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unsoed.appscanning.data.Reminder
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private var reminders: MutableList<Reminder>,
    private val onItemClick: (Reminder) -> Unit,
    private val onDeleteClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.tvTitle.text = reminder.title

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.tvDate.text = dateFormat.format(Date(reminder.time))

        holder.tvDescription.text = reminder.description

        holder.itemView.setOnClickListener { onItemClick(reminder) }
        holder.btnDelete.setOnClickListener { onDeleteClick(reminder) }
    }

    override fun getItemCount() = reminders.size

    fun removeItem(reminder: Reminder) {
        val index = reminders.indexOf(reminder)
        if (index != -1) {
            reminders.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}