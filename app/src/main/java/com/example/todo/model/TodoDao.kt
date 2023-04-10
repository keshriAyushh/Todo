package com.example.todo.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {

    @Insert
    suspend fun addTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM todos")
    fun getTodos(): LiveData<List<Todo>>

    @Query("DELETE FROM todos")
    fun deleteAll()
}