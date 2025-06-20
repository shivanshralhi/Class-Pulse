package Models

import Activities.TaskActivity
import Database.DatabaseHelper
import Database.Repository
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    private val repository: Repository
    val allTasks : LiveData<List<TaskDataClass>>
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val USER_ID = auth.currentUser?.uid.toString()
    private val db: FirebaseFirestore by lazy { Firebase.firestore}

    init {
        val dao= DatabaseHelper.getDB(application)?.taskDao()
        repository= dao?.let { Repository(it) }!!
        allTasks=repository.allTasks
    }

    fun deleteTask(task: TaskDataClass)= viewModelScope.launch ( Dispatchers.IO ){
        repository.delete(task)
    }

    fun insertTask(task: TaskDataClass)= viewModelScope.launch ( Dispatchers.IO ){
        repository.insert(task)
    }

    fun updateTask(task: TaskDataClass)= viewModelScope.launch ( Dispatchers.IO ){
        repository.update(task)
    }

    fun updateFirebaseTask(task: TaskDataClass, activity: Activity){
        viewModelScope.launch(Dispatchers.IO) {

            db.collection("ASSIGNMENTS").document(USER_ID).collection("USER_ASSIGNMENTS").document(task.firebaseId).set(task)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Assignment Updated", Toast.LENGTH_SHORT).show()
                    activity.startActivity(Intent(activity, TaskActivity::class.java))
                    activity.finish()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
        }

    }

    fun updateFirebaseTaskCheckBox(task: TaskDataClass, activity: Activity){
        viewModelScope.launch(Dispatchers.IO) {

            db.collection("ASSIGNMENTS").document(USER_ID).collection("USER_ASSIGNMENTS").document(task.firebaseId).set(task)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Assignment Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
        }

    }

    fun createFirebaseTask(task: TaskDataClass, activity: Activity){
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("ASSIGNMENTS").document(USER_ID).collection("USER_ASSIGNMENTS").document(task.firebaseId).set(task)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Assignment Created", Toast.LENGTH_SHORT).show()
                    activity.startActivity(Intent(activity, TaskActivity::class.java))
                    activity.finish()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun deleteFirebaseTask(task: TaskDataClass, activity: Activity){
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("ASSIGNMENTS").document(USER_ID).collection("USER_ASSIGNMENTS").document(task.firebaseId).delete()
                .addOnSuccessListener {
                    Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show()
                    activity.startActivity(Intent(activity, TaskActivity::class.java))
                    activity.finish()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
        }
    }



}