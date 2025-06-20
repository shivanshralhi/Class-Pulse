package Activities

import android.content.SharedPreferences

interface SharedPreferenceHandler {

    fun getScheduleID(): Int
    fun updateScheduleID(updatedID: Int)


    fun getAttendanceID(): Int
    fun updateAttendanceID(updatedID: Int)

    fun getUserVersion(): Int
    fun updateUserVersion(updatedVersion: Int)

    fun getNavigationCode(): Int
    fun updateNavigationCode(activityInt: Int)

    fun getAppOpenCount(): Int
    fun updateAppOpenCount(updatedID: Int)

    fun getSharedPreferences(s: String, modePrivate: Int): SharedPreferences

}