package Database

import Models.Attendance
import androidx.lifecycle.LiveData

class AttendanceRepository(private val attendanceDAO: AttendanceDAO) {

    val allAttendance: LiveData<List<Attendance>> = attendanceDAO.getAllAttendance()

    suspend fun insertAttendance(attendance: Attendance){
        attendanceDAO.insertAttendance(attendance)
    }

    suspend fun deleteAttendance(attendance: Attendance){
        attendanceDAO.deleteAttendance(attendance)
    }

    suspend fun updateAttendance(attendance: Attendance){
        attendanceDAO.updateAttendance(attendance.id, attendance.percentage, attendance.subjectName, attendance.classesConducted, attendance.classesAttended, attendance.lastUpdated, attendance.requirement)
    }

}