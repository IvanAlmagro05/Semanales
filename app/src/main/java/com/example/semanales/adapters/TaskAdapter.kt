package com.example.semanales.adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.semanales.R
import com.example.semanales.models.Task
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskStatusChanged: (Int, Boolean) -> Unit,
    private val onTaskDeleted: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view as MaterialCardView
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvDayInitial: TextView = view.findViewById(R.id.tvDayInitial)
        val cbCompleted: CheckBox = view.findViewById(R.id.cbCompleted)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvTitle.text = task.title
        holder.tvDescription.text = task.description
        
        // Configurar la fecha y hora
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = task.date
        
        val pattern = if (task.hasTime) "dd/MM/yyyy HH:mm" else "dd/MM/yyyy"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(task.date))

        // Inicial del día de la semana
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val initial = when (dayOfWeek) {
            Calendar.MONDAY -> "L"
            Calendar.TUESDAY -> "M"
            Calendar.WEDNESDAY -> "X"
            Calendar.THURSDAY -> "J"
            Calendar.FRIDAY -> "V"
            Calendar.SATURDAY -> "S"
            Calendar.SUNDAY -> "D"
            else -> ""
        }
        holder.tvDayInitial.text = initial

        // Aplicar colores y estilos según estado
        if (task.isCompleted) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.taskCardCompleted))
            holder.tvTitle.paintFlags = holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTitle.setTextColor(Color.parseColor("#757575")) // Gris para completado
            holder.cardView.strokeColor = ContextCompat.getColor(holder.itemView.context, R.color.secondaryColor)
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.taskCardPending))
            holder.tvTitle.paintFlags = holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTitle.setTextColor(Color.BLACK)
            holder.cardView.strokeColor = Color.parseColor("#FFCDD2") // Borde rojo suave
        }

        holder.cbCompleted.setOnCheckedChangeListener(null)
        holder.cbCompleted.isChecked = task.isCompleted
        holder.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.id?.let { onTaskStatusChanged(it, isChecked) }
        }

        holder.btnDelete.setOnClickListener {
            onTaskDeleted(task)
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
