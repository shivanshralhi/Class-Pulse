package Models

import Database.AttendanceDAO
import Database.AttendanceRepository
import Database.DatabaseHelper
import Database.Repository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttendanceViewModel(application: Application): AndroidViewModel(application) {

    private val attendanceRepository: AttendanceRepository
    val allAttendance : LiveData<List<Attendance>>

    init {
        val AttendanceDAO= DatabaseHelper.getDB(application)?.attendanceDao()
        attendanceRepository= AttendanceDAO?.let { AttendanceRepository(it)}!!
        allAttendance=attendanceRepository.allAttendance
    }

    fun deleteAttendance(attendance: Attendance)= viewModelScope.launch ( Dispatchers.IO ){
        attendanceRepository.deleteAttendance(attendance)
    }

    fun insertAttendance(attendance: Attendance)= viewModelScope.launch(Dispatchers.IO) {
        attendanceRepository.insertAttendance(attendance)
    }

    fun updateAttendance(attendance: Attendance)= viewModelScope.launch(Dispatchers.IO) {
        attendanceRepository.updateAttendance(attendance)
    }


}