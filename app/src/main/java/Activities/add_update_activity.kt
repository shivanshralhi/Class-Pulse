package Activities

import Models.Attendance
import Database.DatabaseHelper
import Models.AttendanceViewModel
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.collegetracker.R
import com.collegetracker.databinding.ActivityAddUpdateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class add_update_activity : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    lateinit var binding:ActivityAddUpdateBinding
    lateinit var viewModel: AttendanceViewModel
    lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { Firebase.firestore}

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        auth=FirebaseAuth.getInstance()
        val USER_ID= auth.currentUser?.uid.toString()

        //Initialization of Database
        database = DatabaseHelper.getDB(applicationContext) ?: throw IllegalStateException("Unable to create database instance")

        // Attendance ViewModel
        viewModel= ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application))
            .get(AttendanceViewModel::class.java)

        //Receiving subject data through intent
        val subjectID=intent.getIntExtra("subjectId",1)
        val firebaseId=intent.getStringExtra("firebaseId")
        var subjectName=intent.getStringExtra("subject")
        val conducted=intent.getStringExtra("conducted")
        val attended=intent.getStringExtra("attended")
        var lastUpdated=intent.getStringExtra("lastUpdated")
        val requirement=intent.getStringExtra("requirement")!!

        var percentage="0"
        if(conducted!=null && attended!=null){
            percentage=(((attended.toDouble()/conducted.toDouble())*100).toInt()).toString()+"%"
        }


        binding.subjectName.setText(subjectName)
        binding.conductedEt.setText(conducted)
        binding.attendedEt.setText(attended)
        binding.percentage.setText(percentage)
        binding.lastUpdated.setText("Last Updated: $lastUpdated")
        binding.requirementEt.setText(requirement)

        if(conducted!=null && attended!=null){

            var tempConducted= conducted.toInt()
            var tempAttended= attended.toInt()
            var tempRequirement= requirement?.replace("%","")?.toInt()


            //CASSES REQUIRED CALCULATION
            classesRequired(conducted, attended, percentage ,requirement)


            binding.minusConducted.setOnClickListener {
                vibrator?.vibrate(50)
                tempConducted--
                binding.conductedEt.setText(tempConducted.toString())
                percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                binding.percentage.setText(percentage)

                //CASSES REQUIRED CALCULATION
                classesRequired(tempConducted.toString(), tempAttended.toString(), percentage ,tempRequirement.toString())
            }

            binding.plusConducted.setOnClickListener {
                vibrator?.vibrate(50)
                tempConducted++
                binding.conductedEt.setText(tempConducted.toString())
                percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                binding.percentage.setText(percentage)

                //CASSES REQUIRED CALCULATION
                classesRequired(tempConducted.toString(), tempAttended.toString(), percentage ,tempRequirement.toString())
            }

            binding.minusAttended.setOnClickListener {
                vibrator?.vibrate(50)
                tempAttended--
                binding.attendedEt.setText(tempAttended.toString())
                percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                binding.percentage.setText(percentage)

                //CASSES REQUIRED CALCULATION
                classesRequired(tempConducted.toString(), tempAttended.toString(), percentage ,tempRequirement.toString())
            }

            binding.plusAttended.setOnClickListener {
                vibrator?.vibrate(50)
                tempAttended++
                binding.attendedEt.setText(tempAttended.toString())
                percentage=(((tempAttended.toDouble()/tempConducted.toDouble())*100).toInt()).toString()+"%"
                binding.percentage.setText(percentage)

                //CASSES REQUIRED CALCULATION
                classesRequired(tempConducted.toString(), tempAttended.toString(), percentage ,tempRequirement.toString())
            }

            binding.minusRequirement.setOnClickListener {
                vibrator?.vibrate(50)
                tempRequirement= tempRequirement?.minus(5)
                binding.requirementEt.setText("$tempRequirement%")

                //CASSES REQUIRED CALCULATION
                classesRequired(tempConducted.toString(), tempAttended.toString(), percentage ,tempRequirement.toString())
            }

            binding.plusRequirement.setOnClickListener {
                vibrator?.vibrate(50)
                tempRequirement= tempRequirement?.plus(5)
                binding.requirementEt.setText("$tempRequirement%")

                //CASSES REQUIRED CALCULATION
                classesRequired(tempConducted.toString(), tempAttended.toString(), percentage ,tempRequirement.toString())
            }


            //UPDATE BUTTON
            binding.updateButton.setOnClickListener {
                vibrator.vibrate(50)

                val subjectName=binding.subjectName.text.toString()
                val conductedName=binding.conductedEt.text.toString()
                val attendedName=binding.attendedEt.text.toString()
                val percentageName=binding.percentage.text.toString()
                val requirementName=binding.requirementEt.text.toString()
                val currentTime=currentTime().toString()


                if(auth.currentUser==null){
                    //Toast.makeText(this, "user is null", Toast.LENGTH_SHORT).show()
                    val attendance = Attendance(subjectID, "", percentageName, subjectName, conductedName, attendedName, currentTime, requirementName)
                    viewModel.updateAttendance(attendance)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else{
                    val ATTENDANCE_ID = firebaseId!!
                    val attendance = Attendance(subjectID, ATTENDANCE_ID, percentageName, subjectName, conductedName, attendedName, currentTime, requirementName)
                    Log.d("FirestoreDebug", "ATTENDANCE_ID in updatescreen: $ATTENDANCE_ID")
                    db.collection("ATTENDANCE").document(USER_ID).collection("USER_ATTENDANCE").document(ATTENDANCE_ID).set(attendance)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                            val intent =  Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Some error occured while Saving", Toast.LENGTH_SHORT).show()
                        }
                }

            }

        }

        //ON BACK-PRESSES
        val onBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                vibrator?.vibrate(50)
                startActivity(Intent(this@add_update_activity, MainActivity::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun classesRequired( conducted: String, attended: String, percentage:String, requirement: String ) {
        val conductedInt=conducted.toDouble()
        val attendedInt=attended.toDouble()
        val requirementInt= requirement.replace("%","").toDouble()
        val percentageInt= percentage.replace("%", "").toDouble()

        var classesRequired=0;
        if(percentageInt < requirementInt){
            classesRequired= Math.ceil((( requirementInt * conductedInt) - (100.0 * attendedInt))/(100.0- requirementInt)).toInt()

            binding.classesRequired.setText(classesRequired.toString())
            binding.classesRequiredStatus.setText("Classes Needed")

            binding.classesRequiredCardview.setCardBackgroundColor(getResources().getColor(R.color.light_red));

        }
        else if(percentageInt > requirementInt){

            classesRequired=Math.floor(((100 * attendedInt)-(requirementInt * conductedInt))/requirementInt).toInt()
            binding.classesRequired.setText(classesRequired.toString())
            binding.classesRequiredStatus.setText("Classes can be Missed")

            binding.classesRequiredCardview.setCardBackgroundColor(getResources().getColor(R.color.light_green));


        }
        else if(percentageInt == requirementInt){
            binding.classesRequired.setText("0")
            binding.classesRequiredStatus.setText("Classes can be Missed")
        }

        if(classesRequired>=1000){
            binding.classesRequired.setText("♾")
        }

    }

    private fun currentTime(): String {
        val currentDateTime = LocalDateTime.now()

        // Format the date and time with the desired pattern (dd-MM HH:mm)
        val formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm")
        val DateTime = currentDateTime.format(formatter).toString()

        return DateTime
    }

}