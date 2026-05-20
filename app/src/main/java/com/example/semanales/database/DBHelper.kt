package com.example.ofijaensat.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ofijaensat.models.Task

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TasksDB"
        private const val DATABASE_VERSION = 3
        private const val TABLE_TASKS = "tasks"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_IS_COMPLETED = "isCompleted"
        private const val KEY_HAS_TIME = "hasTime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_TASKS (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_TITLE TEXT," +
                "$KEY_DESCRIPTION TEXT," +
                "$KEY_DATE INTEGER," +
                "$KEY_IS_COMPLETED INTEGER DEFAULT 0," +
                "$KEY_HAS_TIME INTEGER DEFAULT 0)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $KEY_IS_COMPLETED INTEGER DEFAULT 0")
        }
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $TABLE_TASKS ADD COLUMN $KEY_HAS_TIME INTEGER DEFAULT 0")
        }
    }

    fun addTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TITLE, task.title)
            put(KEY_DESCRIPTION, task.description)
            put(KEY_DATE, task.date)
            put(KEY_IS_COMPLETED, if (task.isCompleted) 1 else 0)
            put(KEY_HAS_TIME, if (task.hasTime) 1 else 0)
        }
        val success = db.insert(TABLE_TASKS, null, values)
        db.close()
        return success
    }

    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        db.update(TABLE_TASKS, values, "$KEY_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    fun deleteTask(taskId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, "$KEY_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    fun getTasksByDateRange(startDate: Long, endDate: Long): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_TASKS WHERE $KEY_DATE BETWEEN ? AND ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(startDate.toString(), endDate.toString()))

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(KEY_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_COMPLETED)) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HAS_TIME)) == 1
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return taskList
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS", null)
        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(KEY_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_COMPLETED)) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HAS_TIME)) == 1
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return taskList
    }
}
