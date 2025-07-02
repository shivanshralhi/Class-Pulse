package Functions

import Activities.sharedPreferenceManager
import Adapters.RecyclerAttendanceAdapter
import Database.DatabaseHelper
import Models.Attendance
import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForNewUser(context: Context) {

    private lateinit var sharedPreferenceManager: sharedPreferenceManager

    //SHARED PREFERENCE FOR NEW USER, EXECUTES ONE TIME ONLY
    fun sharedPreferenceForNewUser(
        context: Context,
        arrAttendance:ArrayList<Attendance>,
        database: DatabaseHelper,
        adapter: RecyclerAttendanceAdapter
    ){

        sharedPreferenceManager=sharedPreferenceManager(context)
        // SharedPreference for new User
        if(sharedPreferenceManager.getUserVersion()==0){
            // Adding data to the Database
            val currentTime=currentTime().toString()

            arrAttendance.add(Attendance(1, "","100%", "Subject 1", "10", "10", currentTime, "75%"))
            arrAttendance.add(Attendance(2,"", "60%", "Subject 2", "10", "6", currentTime, "75%"))
            arrAttendance.add(Attendance(3, "","40%", "Subject 3", "10", "4", currentTime, "75%"))

            GlobalScope.launch {
                database.attendanceDao().insertAttendance(Attendance(1, "","100%", "Subject 1", "10", "10", currentTime, "75%"))
                database.attendanceDao().insertAttendance(Attendance(2, "","60%", "Subject 2", "10", "6", currentTime,"75%" ))
                database.attendanceDao().insertAttendance(Attendance(3, "","40%", "Subject 3", "10", "4", currentTime, "75%"))


            }

            adapter.notifyItemChanged(arrAttendance.size-1)
            sharedPreferenceManager.updateUserVersion(1)


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