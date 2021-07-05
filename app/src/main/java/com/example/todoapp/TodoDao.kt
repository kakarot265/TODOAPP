package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTask(todoModel: TodoModel):Long

    @Query("Select *From TodoModel where isFinished ==0")
    fun getTask():LiveData<List<TodoModel>>

    @Query("Update TodoModel SET isFinished=1 where id =:uid")
    fun finishedTask(uid:Long)

    @Query("Delete From TodoModel where id =:uid")
    fun deleteTask(uid:Long)
}