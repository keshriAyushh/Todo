package com.example.todo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.RecyclerViewClicksInterface
import com.example.todo.model.Todo
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class TodoAdapter(
    private val context: Context,
    private val todos: List<Todo>,
    private val recyclerViewClickListener: RecyclerViewClicksInterface
): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.todo_items, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.priority.text = todos[position].priority
        holder.date.text = todos[position].date

        holder.delete.setOnClickListener {
            recyclerViewClickListener.onItemDeleteBtnClick(todos[position])
        }
        holder.title.text = todos[position].title
    }

    override fun getItemCount() = todos.size

    class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tvTodoTitle)
        val priority: TextView = itemView.findViewById(R.id.tvPriority)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val checked: CheckBox = itemView.findViewById(R.id.cbDone)
        val delete: ExtendedFloatingActionButton = itemView.findViewById(R.id.btnDelete)

    }
}