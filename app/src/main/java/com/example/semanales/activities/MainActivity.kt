package com.example.ofijaensat.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ofijaensat.R
import com.example.ofijaensat.adapters.CalendarAdapter
import com.example.ofijaensat.adapters.TaskAdapter
import com.example.ofijaensat.database.DBHelper
import com.example.ofijaensat.models.Task
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var tvWeekRange: TextView
    private lateinit var tvMonthName: TextView
    
    private val localeES = Locale("es", "ES")
    private var currentWeekCalendar = Calendar.getInstance(localeES)
    private var currentMonthCalendar = Calendar.getInstance(localeES)

    override fun attachBaseContext(newBase: Context) {
        val locale = Locale("es", "ES")
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Locale.setDefault(localeES)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        val mainLayout = findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)
        
        // Setup Task List
        val rvTasks = findViewById<RecyclerView>(R.id.rvTasks)
        tvWeekRange = findViewById(R.id.tvWeekRange)
        
        taskAdapter = TaskAdapter(emptyList(), 
            onTaskStatusChanged = { taskId, isCompleted ->
                dbHelper.updateTaskStatus(taskId, isCompleted)
                updateUI()
            },
            onTaskDeleted = { task ->
                showDeleteConfirmDialog(task)
            }
        )
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter

        // Setup Calendar
        val rvCalendar = findViewById<RecyclerView>(R.id.rvCalendar)
        tvMonthName = findViewById(R.id.tvMonthName)
        calendarAdapter = CalendarAdapter(emptyList(), emptyList(), -1) { selectedDate ->
            currentWeekCalendar.time = selectedDate
            // Ajustar al inicio de la semana (Lunes)
            while (currentWeekCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                currentWeekCalendar.add(Calendar.DAY_OF_MONTH, -1)
            }
            updateUI()
        }
        rvCalendar.layoutManager = GridLayoutManager(this, 7)
        rvCalendar.adapter = calendarAdapter

        // Navigation buttons
        findViewById<ImageButton>(R.id.btnPrevWeek).setOnClickListener {
            currentWeekCalendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateUI()
        }
        findViewById<ImageButton>(R.id.btnNextWeek).setOnClickListener {
            currentWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateUI()
        }
        findViewById<ImageButton>(R.id.btnPrevMonth).setOnClickListener {
            currentMonthCalendar.add(Calendar.MONTH, -1)
            updateUI()
        }
        findViewById<ImageButton>(R.id.btnNextMonth).setOnClickListener {
            currentMonthCalendar.add(Calendar.MONTH, 1)
            updateUI()
        }
        
        findViewById<ExtendedFloatingActionButton>(R.id.btnAddTask).setOnClickListener {
            showAddTaskDialog()
        }

        // Inicializar al lunes de la semana actual
        while (currentWeekCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            currentWeekCalendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        updateUI()
    }

    private fun updateUI() {
        val tempCal = currentWeekCalendar.clone() as Calendar
        while (tempCal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            tempCal.add(Calendar.DAY_OF_MONTH, -1)
        }
        
        tempCal.set(Calendar.HOUR_OF_DAY, 0)
        tempCal.set(Calendar.MINUTE, 0)
        tempCal.set(Calendar.SECOND, 0)
        tempCal.set(Calendar.MILLISECOND, 0)
        val startOfWeek = tempCal.timeInMillis

        tempCal.add(Calendar.DAY_OF_YEAR, 6)
        tempCal.set(Calendar.HOUR_OF_DAY, 23)
        tempCal.set(Calendar.MINUTE, 59)
        tempCal.set(Calendar.SECOND, 59)
        tempCal.set(Calendar.MILLISECOND, 999)
        val endOfWeek = tempCal.timeInMillis

        val sdfWeek = SimpleDateFormat("dd MMM", localeES)
        tvWeekRange.text = "${sdfWeek.format(Date(startOfWeek))} - ${sdfWeek.format(Date(endOfWeek))}"

        val weeklyTasks = dbHelper.getTasksByDateRange(startOfWeek, endOfWeek)
        taskAdapter.updateTasks(weeklyTasks.sortedBy { it.date })

        val monthDays = getDaysInMonth(currentMonthCalendar.time)
        val allTasks = dbHelper.getAllTasks()
        calendarAdapter.updateData(monthDays, allTasks, currentMonthCalendar.get(Calendar.MONTH))
        
        val sdfMonth = SimpleDateFormat("MMMM yyyy", localeES)
        tvMonthName.text = sdfMonth.format(currentMonthCalendar.time).replaceFirstChar { it.uppercase() }
    }

    private fun getDaysInMonth(date: Date): List<Date> {
        val days = mutableListOf<Date>()
        val cal = Calendar.getInstance(localeES)
        cal.time = date
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        val firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK)
        val daysBefore = if (firstDayOfMonth == Calendar.SUNDAY) 6 else firstDayOfMonth - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_MONTH, -daysBefore)
        
        for (i in 0 until 42) {
            days.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    private fun showDeleteConfirmDialog(task: Task) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvConfirmMessage)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelDelete)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmDelete)

        tvMessage.text = "¿Deseas eliminar la tarea: \"${task.title}\"?\nEsta acción no se puede deshacer."

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            task.id?.let {
                dbHelper.deleteTask(it)
                updateUI()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val tilTitle = dialogView.findViewById<TextInputLayout>(R.id.tilTitle)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val btnPickDate = dialogView.findViewById<Button>(R.id.btnPickDate)
        val btnPickTime = dialogView.findViewById<Button>(R.id.btnPickTime)
        val tvSelectedDateTime = dialogView.findViewById<TextView>(R.id.tvSelectedDateTime)
        val tvDateError = dialogView.findViewById<TextView>(R.id.tvDateError)
        val cvDateTime = dialogView.findViewById<MaterialCardView>(R.id.cvDateTime)

        val selectedCalendar = Calendar.getInstance(localeES)
        selectedCalendar.set(Calendar.SECOND, 0)
        selectedCalendar.set(Calendar.MILLISECOND, 0)

        var hasTime = false
        var isDatePicked = false
        val sdfWithTime = SimpleDateFormat("dd/MM/yyyy HH:mm", localeES)
        val sdfDateOnly = SimpleDateFormat("dd/MM/yyyy", localeES)

        btnPickDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                isDatePicked = true
                tvDateError.visibility = View.GONE
                cvDateTime.strokeWidth = 0
                
                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)

                val pickedDate = Calendar.getInstance()
                pickedDate.set(year, month, dayOfMonth, 0, 0, 0)
                pickedDate.set(Calendar.MILLISECOND, 0)

                if (pickedDate.before(today)) {
                    Toast.makeText(this, "esta fecha ya ha pasado", Toast.LENGTH_SHORT).show()
                }

                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                if (!hasTime) {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    selectedCalendar.set(Calendar.MINUTE, 0)
                }
                tvSelectedDateTime.text = if (hasTime) sdfWithTime.format(selectedCalendar.time) else sdfDateOnly.format(selectedCalendar.time)
            }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH))
            
            datePickerDialog.datePicker.firstDayOfWeek = Calendar.MONDAY
            datePickerDialog.show()
        }

        btnPickTime.setOnClickListener {
            TimePickerDialog(this, { _, hourOfDay, minute ->
                hasTime = true
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                tvSelectedDateTime.text = sdfWithTime.format(selectedCalendar.time)
            }, selectedCalendar.get(Calendar.HOUR_OF_DAY), selectedCalendar.get(Calendar.MINUTE), true).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // Ponemos null para configurar el listener luego
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        // Sobrescribimos el botón para validar sin cerrar el diálogo automáticamente
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = etTitle.text.toString().trim()
            var isValid = true

            if (title.isEmpty()) {
                tilTitle.error = "El título es obligatorio"
                isValid = false
            } else {
                tilTitle.error = null
            }

            if (!isDatePicked) {
                tvDateError.visibility = View.VISIBLE
                cvDateTime.strokeWidth = 2
                isValid = false
            }

            if (isValid) {
                if (!hasTime) {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    selectedCalendar.set(Calendar.MINUTE, 0)
                }

                dbHelper.addTask(Task(
                    title = title,
                    description = etDescription.text.toString(),
                    date = selectedCalendar.timeInMillis,
                    hasTime = hasTime
                ))
                updateUI()
                dialog.dismiss()
            }
        }
    }
}
