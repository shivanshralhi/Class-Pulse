package Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // Used for Room
    var percentage: String = "",
    var subjectName: String = "",
    var classesConducted: String = "",
    var classesAttended: String = "",
    var lastUpdated: String = "",
    var requirement: String = ""
) : Serializable {


    @Ignore
    var firebaseId: String = "" // Ignored by Room
    // Secondary constructor for Firebase usage
    @Ignore
    constructor(
        id: Int,
        firebaseId: String,
        percentage: String,
        subjectName: String,
        classesConducted: String,
        classesAttended: String,
        lastUpdated: String,
        requirement: String
    ) : this(id, percentage, subjectName, classesConducted, classesAttended, lastUpdated, requirement) {
        this.firebaseId = firebaseId
    }
}
