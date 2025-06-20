package Functions

import Activities.Daily_Schedule
import Activities.TaskActivity
import Activities.sharedPreferenceManager
import android.content.Context
import android.content.Intent

class StartupActivity(context: Context) {
    private lateinit var sharedPreferenceManager: sharedPreferenceManager


    fun openStartupActivity(context: Context) {
        sharedPreferenceManager=sharedPreferenceManager(context)

        val activityCode =  sharedPreferenceManager.getNavigationCode()
        if(activityCode==1){
            // MainAcvity Remains Open
        }
        else if(activityCode==2){
            val intent = Intent(context, Daily_Schedule::class.java)
            context.startActivity(intent)
        }
        else if(activityCode==3){
            val intent = Intent(context, TaskActivity::class.java)
            context.startActivity(intent)
        }


    }
}