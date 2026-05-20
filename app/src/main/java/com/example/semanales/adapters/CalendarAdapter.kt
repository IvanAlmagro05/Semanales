package com.example.ofijaensat.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ofijaensat.R
import com.example.ofijaensat.models.Task
import java.util.*

class CalendarAdapter(
    private var days: List<Date>,
    private var tasks: List<Task>,
    private var currentMonth: Int = -1,
    private val onDayClick: (Date) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayNumber: TextView = view.findViewById(R.id.tvDayNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        val calendar = Calendar.getInstance()
        calendar.time = date
        
        holder.tvDayNumber.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        // Atenuar días que no pertenecen al mes actual
        if (currentMonth != -1 && calendar.get(Calendar.MONTH) != currentMonth) {
            holder.tvDayNumber.alpha = 0.3f
        } else {
            holder.tvDayNumber.alpha = 1.0f
        }

        val dayTasks = tasks.filter { isSameDay(it.date, date.time) }
        val background = holder.tvDayNumber.background as GradientDrawable
        
        if (dayTasks.isNotEmpty()) {
            val allCompleted = dayTasks.all { it.isCompleted }
            if (allCompleted) {
                background.setColor(ContextCompat.getColor(holder.itemView.context, R.color.taskCompleted))
                holder.tvDayNumber.setTextColor(Color.BLACK)
            } else {
                background.setColor(ContextCompat.getColor(holder.itemView.context, R.color.taskPending))
                holder.tvDayNumber.setTextColor(Color.BLACK)
            }
        } else {
            background.setColor(Color.TRANSPARENT)
            holder.tvDayNumber.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener { onDayClick(date) }
    }

    override fun getItemCount() = days.size

    fun updateData(newDays: List<Date>, newTasks: List<Task>, newMonth: Int) {
        days = newDays
        tasks = newTasks
        currentMonth = newMonth
        notifyDataSetChanged()
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
