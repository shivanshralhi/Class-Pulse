package Adapters


import Activities.MainActivity
import Activities.add_update_activity
import Database.DatabaseHelper
import Models.Attendance
import Models.AttendanceViewModel
import Models.AttendenceModel
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.collegetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecyclerAttendanceAdapter(val context: Context,val arrAttendance: ArrayList<Attendance>, private val viewModel: AttendanceViewModel)
    : RecyclerView.Adapter<RecyclerAttendanceAdapter.ViewHolder>() {


    //Initialization of Database
    private var database: DatabaseHelper
    init {
        // Initialize the database using the context
        database = Room.databaseBuilder(
            context.applicationContext,
            DatabaseHelper::class.java,
            "CollegeTracker_DB"
        ).build()
    }

    lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { Firebase.firestore}

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        // subject, conductNumber, attendNumber (FROM THE attendance_row LAYOUT)
        val percentage=itemView.findViewById<TextView>(R.id.percentage)
        val subject = itemView.findViewById<TextView>(R.id.subject)
        val conductNumber=itemView.findViewById<TextView>(R.id.conductNumber)
        val attendNumber=itemView.findViewById<TextView>(R.id.attendNumber)
        val progressBar=itemView.findViewById<CircularProgressBar>(R.id.circularProgressBar)


        val recyclerLayout=itemView.findViewById<LinearLayout>(R.id.recyclerLayout)
        //VIBRATOR VIBRATOR VIBRATOR
        val vibrator = itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_attendance, parent, false))

    }

    override fun getItemCount(): Int {
        return arrAttendance.size


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentSubject= arrAttendance[position]

        auth=FirebaseAuth.getInstance()
        holder.percentage.text=arrAttendance[position].percentage
        holder.subject.text=arrAttendance[position].subjectName
        holder.attendNumber.text=arrAttendance[position].classesAttended
        holder.conductNumber.text=arrAttendance[position].classesConducted

        holder.progressBar.apply {
            val percentFloat=arrAttendance[position].percentage.replace("%","").toFloat()
            setProgressWithAnimation(percentFloat, 1000)
            roundBorder=true
        }

        holder.recyclerLayout.setOnClickListener {
            holder.vibrator.vibrate(50)
            val intent= Intent(context, add_update_activity::class.java)
            intent.putExtra("subjectId",arrAttendance[position].id)
            intent.putExtra("firebaseId", arrAttendance[position].firebaseId)
            intent.putExtra("subject",arrAttendance[position].subjectName)
            intent.putExtra("conducted",arrAttendance[position].classesConducted)
            intent.putExtra("attended",arrAttendance[position].classesAttended)
            intent.putExtra("lastUpdated", arrAttendance[position].lastUpdated)
            intent.putExtra("requirement", arrAttendance[position].requirement)
            Log.d("FirestoreDebug", "ATTENDANCE_ID in adapter: ${arrAttendance[position].firebaseId}")

            context.startActivity(intent)
            (context as Activity).finish()

        }


        holder.recyclerLayout.setOnLongClickListener{
            // Toast.makeText(context, "hiii", Toast.LENGTH_SHORT).show()
            holder.vibrator.vibrate(50)

            val builder = AlertDialog.Builder(context)
                .setTitle("Delete Subject")
                .setIcon(R.drawable.baseline_delete_24)
                .setMessage("Do you want to Delete this Subject ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    try {

                        val USER_ID= auth.currentUser?.uid.toString()
                        val firebaseId = arrAttendance[position].firebaseId
                        if(auth.currentUser==null){
                            Toast.makeText(context, "Subject Deleted", Toast.LENGTH_SHORT).show()
                            viewModel.deleteAttendance(currentSubject)
                            notifyItemRemoved(position)
                        }
                        else{
                            db.collection("ATTENDANCE").document(USER_ID).collection("USER_ATTENDANCE").document(firebaseId).delete()
                                .addOnSuccessListener {
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                    if (context is Activity) {
                                        context.finish()
                                    }

                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error Deleting Subject", Toast.LENGTH_SHORT).show()
                                }
                        }


                    } catch (e: Exception) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        Log.w("crash-attendance", e)
                    }

                }.setNegativeButton("No")
                { dialogInterface, i ->
                    Toast.makeText(context, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                }
            builder.show()
            true

        }
    }

    private fun currentTime(): String? {
        val currentDateTime = LocalDateTime.now()

        // Format the date and time with the desired pattern (dd-MM HH:mm)
        val formatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm")
        val DateTime = currentDateTime.format(formatter).toString()

        return DateTime
    }

}