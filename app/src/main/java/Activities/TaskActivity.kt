package Activities

import Adapters.RecyclerTaskAdapter
import Database.DatabaseHelper
import Functions.GoogleAuthentication
import Models.TaskDataClass
import Models.TaskViewModel
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.collegetracker.R
import com.collegetracker.databinding.ActivityTaskBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class TaskActivity : AppCompatActivity(){

    private lateinit var binding: ActivityTaskBinding
    private lateinit var database: DatabaseHelper
    lateinit var viewModel: TaskViewModel
    lateinit var adapter: RecyclerTaskAdapter
    lateinit var selectedTask: TaskDataClass
    lateinit var vibrator: Vibrator
    private lateinit var sharedPreferenceManager:sharedPreferenceManager
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val USER_ID= auth.currentUser?.uid.toString()
    private val db: FirebaseFirestore by lazy { Firebase.firestore}
    private val googleAuthentication: GoogleAuthentication by lazy { GoogleAuthentication(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        sharedPreferenceManager=sharedPreferenceManager(this)
        database= DatabaseHelper.getDB(this)!!
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val googleCardView = findViewById<CardView>(R.id.GoogleCardView)

        viewModel= ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application))
            .get(TaskViewModel::class.java)

        // Initializing the UI
        initUI()

        // FETCHING ASSIGNMENTS
        binding.progressBar.visibility = View.VISIBLE
        if(auth.currentUser==null){

            viewModel.allTasks.observe(this) { list->
                list?.let{
                    val sortedList = it.sortedBy { task -> task.submissionISODate }
                    adapter.updateList(sortedList)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        else{
            val reference = db.collection("ASSIGNMENTS").document(USER_ID).collection("USER_ASSIGNMENTS")
            reference.orderBy("submissionISODate", Query.Direction.ASCENDING).get()
                .addOnSuccessListener {
                    val assignmentList = mutableListOf<TaskDataClass>()
                    for (document in it.documents) {
                        val firebaseId = document.getString("firebaseId") ?: ""
                        val taskName = document.getString("taskName") ?: ""
                        val submissionDate = document.getString("submissionDate") ?: ""
                        val submissionISODate = document.getString("submissionISODate") ?: ""
                        val priority = document.getString("priority") ?: ""
                        val taskDetails = document.getString("taskDetails") ?: ""
                        val isCompleted = document.getBoolean("isComplete") ?: false

                        assignmentList.add(TaskDataClass(0, firebaseId, taskName, submissionDate, submissionISODate, priority, taskDetails, isCompleted))

                    }

                    adapter.updateList(assignmentList)
                    binding.progressBar.visibility = View.GONE

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
        }


        // Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.Tasks_btmNavigation)
        binding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){

                R.id.Attendance_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(1)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                R.id.Schedule_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(2)
                    startActivity(Intent(this, Daily_Schedule::class.java))
                    finish()
                }

            }

            return@setOnItemSelectedListener true
        }

        // Fetch User Profile Image
        val user = auth.currentUser
        if(user!=null){
            val photoUrl = user.photoUrl
            if(photoUrl!=null){
                Glide.with(this@TaskActivity)
                    .load(photoUrl)
                    .into(profileImage)
            }

        }

        googleCardView.setOnClickListener {
            vibrator.vibrate(50)
            googleAuthentication.googleAuth(this)
        }



    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter= RecyclerTaskAdapter(this, viewModel)
        binding.recyclerView.adapter= adapter


        binding.floatingActionButton.setOnClickListener {
            vibrator.vibrate(50)
            val intent= Intent(this, AddUpdateTasks::class.java)
            intent.putExtra("isUpdate", false)
            startActivity(intent)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 888 && resultCode ==  RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            googleAuthentication.firebaseAuthWithGoogle(account.idToken!!, this)
        }

    }
}