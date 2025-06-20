package Models


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleDataclass(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var day: String,
    var lecture: String,
    var time: String) {


    // Secondary constructor 2
    @Ignore
    constructor(day: String, lecture: String, time: String)
            : this(0,day = day, lecture = lecture, time = time)

}
