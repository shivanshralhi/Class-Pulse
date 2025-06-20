package Database

import Models.TaskDataClass
import androidx.lifecycle.LiveData

class Repository(private val taskDao: TaskDAO) {

    val allTasks: LiveData<List<TaskDataClass>> = taskDao.getAllTask()

    suspend fun insert(task: TaskDataClass){
        taskDao.insert(task)
    }

    suspend fun delete(task: TaskDataClass){
        taskDao.delete(task)
    }

    suspend fun update(task: TaskDataClass){
        taskDao.update(task.id, task.taskName, task.submissionDate, task.submissionISODate ,task.priority, task.taskDetails, task.isComplete)
    }


}