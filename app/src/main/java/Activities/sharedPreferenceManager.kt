package Activities

import android.content.Context
import android.content.SharedPreferences



class sharedPreferenceManager(private val context : Context): SharedPreferenceHandler {

    override fun getScheduleID(): Int {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        return pref.getInt("scheduleID", 100)
    }

    override fun updateScheduleID(updatedID: Int) {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("scheduleID", updatedID)
        editor.apply()
    }

    override fun getAttendanceID(): Int{
        val pref2: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        return pref2.getInt("attendanceID",100)
    }

    override fun updateAttendanceID(updatedID: Int){
        val pref2: SharedPreferences=getSharedPreferences("COLLEGE_TRACKER",Context.MODE_PRIVATE)
        val editor=pref2.edit()
        editor.putInt("attendanceID", updatedID)
        editor.apply()
    }

    override fun getUserVersion(): Int {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        return pref.getInt("userVersion", 0)
    }

    override fun updateUserVersion(updatedVersion: Int) {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("userVersion", updatedVersion)
        editor.apply()
    }

    // which Activity to open when the app starts
    override fun getNavigationCode(): Int {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        return pref.getInt("navigationCode", 1)
    }

    override fun updateNavigationCode(activityInt: Int) {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("navigationCode", activityInt)
        editor.apply()
    }

    override fun getAppOpenCount(): Int {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        return pref.getInt("appOpenCount", 0)
    }

    override fun updateAppOpenCount(count: Int) {
        val pref: SharedPreferences = getSharedPreferences("COLLEGE_TRACKER", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("appOpenCount", count)
        editor.apply()
    }

    override fun getSharedPreferences(s: String, modePrivate: Int): SharedPreferences {
        return context.getSharedPreferences(s, modePrivate)
    }


}