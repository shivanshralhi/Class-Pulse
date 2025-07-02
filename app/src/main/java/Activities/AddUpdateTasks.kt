package Activities

import Adapters.customSpinnerAdapter
import Models.TaskDataClass
import Models.TaskViewModel
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.collegetracker.R
import com.collegetracker.databinding.ActivityAddUpdateTasksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddUpdateTasks : AppCompatActivity() {

    private lateinit var binding: ActivityAddUpdateTasksBinding
    private lateinit var task: TaskDataClass
    lateinit var viewModel: TaskViewModel
    lateinit var vibrator: Vibrator
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAddUpdateTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        viewModel= ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application)).get(TaskViewModel::class.java)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val USER_ID= auth.currentUser?.uid.toString()
        spinner()

        var isUpdate  = false
        var id=-1
        var firebaseId = ""
        var name=""
        var submissionDate=""
        var isoDate=""
        var priority=""
        var details=""
        var isComplete=false

        try{
            isUpdate=intent.getBooleanExtra("isUpdate", false)

            if(isUpdate==true){
                id=intent.getIntExtra("id", -1)
                firebaseId = intent.getStringExtra("firebaseId").toString()
                binding.title.text = "Update Assignment"
                name= intent.getStringExtra("name").toString()
                submissionDate= intent.getStringExtra("submissionDate").toString()
                isoDate= intent.getStringExtra("submissionISODate").toString()
                priority= intent.getStringExtra("priority").toString()
                details= intent.getStringExtra("details").toString()
                isComplete= intent.getBooleanExtra("isComplete", false)

                binding.taskName.setText(name)
                binding.submissionDate.text = submissionDate
                binding.taskDetails.setText(details)
                binding.saveButton.text = "Update Task"
            }

//            old_task=intent.getSerializableExtra("current_task") as TaskDataClass
//            binding.taskName.setText(old_task.taskName)
//            binding.taskDetails.setText(old_task.taskDetails)
//            binding.submissionDate.setText(old_task.submissionDate)

//            if(old_task.priority=="Low")
//                binding.priority.setSelection(1)
//            else if(old_task.priority == "Medium")
//                binding.priority.setSelection(2)
//            else if (old_task.priority == "High")
//                binding.priority.setSelection(3)
//            else binding.priority.setSelection(1)

//            isUpdate=true

        }catch (e: Exception){
            e.printStackTrace()
        }

        binding.submissionDate.setOnClickListener {
            showDatePicker(this){ date->
                binding.submissionDate.text = date
            }

        }

        binding.saveButton.setOnClickListener {
            vibrator.vibrate(50)

            val name=binding.taskName.text.toString()
            var dateOfSubmission = binding.submissionDate.text.toString()
            val details= binding.taskDetails.text.toString()
            val priority= binding.priority.selectedItem.toString()


            if(name.isNotEmpty()){

                if(dateOfSubmission.isEmpty()){
                    dateOfSubmission = getTodayDate()
                }

                isoDate = convertCustomToIsoFormat(dateOfSubmission)

                if(isUpdate==true){  // UPDATE THE EXISTING ASSIGNMENT

                    if(auth.currentUser==null){

                        task=TaskDataClass(id,"", name, dateOfSubmission, isoDate, "Low", details, isComplete)
                        viewModel.updateTask(task)

                        Toast.makeText(this, "Assignment Updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, TaskActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    else{
                        task=TaskDataClass(0,firebaseId, name, dateOfSubmission, isoDate, "Low", details, isComplete)
                        viewModel.updateFirebaseTask(task, this@AddUpdateTasks)
                    }
                }
                else{ // CREATE A NEW ASSIGNMENT
                    if(auth.currentUser==null){
                        task=TaskDataClass(null, name, dateOfSubmission, isoDate, "Low", details, false)
                        viewModel.insertTask(task)

                        Toast.makeText(this, "Assignment Created", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, TaskActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val firebaseId = db.collection("ASSIGNMENTS").document(USER_ID).collection("USER_ASSIGNMENTS").document().id
                        task=TaskDataClass(0,firebaseId, name, dateOfSubmission, isoDate, "Low", details, isComplete)
                        viewModel.createFirebaseTask(task, this@AddUpdateTasks)
                    }

                }
            }
            else{
                Toast.makeText(this, "Please Enter Subject", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun spinner() {
        val list_of_priority= arrayListOf("Low", "Medium", "High")
        val prioritySpinnerAdapter= customSpinnerAdapter(this, list_of_priority)
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priority.adapter=prioritySpinnerAdapter
    }

    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Format the date in ISO format (yyyy-MM-dd)
                val isoFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                val selectedDate = isoFormat.format(calendar.time)

                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    fun convertCustomToIsoFormat(customDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = inputFormat.parse(customDate)
        return outputFormat.format(date!!)
    }


    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }


}