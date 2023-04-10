package com.example.todo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.adapters.TodoAdapter
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.model.Priority
import com.example.todo.model.Todo
import com.example.todo.model.TodoDatabase
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity(), RecyclerViewClicksInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: TodoDatabase
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var spinner: Spinner
    private lateinit var title: String
    private lateinit var todos: List<Todo>
    private lateinit var todoAdapter: TodoAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = TodoDatabase.getDatabase(this)

        var priority: String? = null
        getTodos()

        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val monthNum = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)
        val month = resources.getStringArray(R.array.Months)[monthNum+1]

        binding.tvDate.text = "$day $month $year"

        binding.btnAddTodo.setOnClickListener {

            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)

            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .show()

            saveBtn = dialogView.findViewById(R.id.btnSave)
            cancelBtn = dialogView.findViewById(R.id.btnCancel)
            spinner = dialogView.findViewById(R.id.spinner)

            populateSpinner(spinner)
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    priority = resources.getStringArray(R.array.Priorities)[position]
                    if(priority.equals("High")){
                        priority = Priority.HIGH
                    } else if(priority.equals("Medium")){
                        priority = Priority.MEDIUM
                    } else {
                        priority = Priority.LOW
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    priority = null
                }
            }


            saveBtn.setOnClickListener {
                if(dialogView.findViewById<TextInputEditText>(R.id.etTitle).text.toString().isNotEmpty() && !priority.isNullOrEmpty()){
                    //fetch date and time
                    val calendar = Calendar.getInstance()

                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val month = calendar.get(Calendar.MONTH)
                    val year = calendar.get(Calendar.YEAR)

                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val min = calendar.get(Calendar.MINUTE)

                    val time = "$hour:$min $day-$month-$year"

                    title = dialogView.findViewById<TextInputEditText>(R.id.etTitle).text.toString()

                    GlobalScope.launch(Dispatchers.IO) {
                        database.todoDao().addTodo(Todo(0, title, priority!!, time))
                    }
                } else {
                    Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
                }
                builder.dismiss()

            }
            cancelBtn.setOnClickListener{
                builder.dismiss()
            }
        }

        binding.btnClearAll.setOnClickListener {
            clearAll()
        }
    }

    private fun getTodos(){
        database.todoDao().getTodos().observe(this){
            todos = it
            todoAdapter = TodoAdapter(this, todos, this)
            binding.rvTodoItems.adapter = todoAdapter
            binding.rvTodoItems.layoutManager = LinearLayoutManager(this)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onItemDeleteBtnClick(todo: Todo) {
        GlobalScope.launch(Dispatchers.IO) {
            database.todoDao().deleteTodo(todo)
            withContext(Dispatchers.Main){
                todoAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAll(){
        if(todos.isNotEmpty()){
            GlobalScope.launch(Dispatchers.IO){
                database.todoDao().deleteAll()
                withContext(Dispatchers.Main){
                    todoAdapter.notifyDataSetChanged()
                    Toast.makeText(this@MainActivity, "Cleared all Todos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateSpinner(spinner: Spinner){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Priorities))
        spinner.adapter = adapter
    }

}