package Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import java.io.Serializable

@Entity(tableName = "task_table")
data class TaskDataClass(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "taskName") val taskName: String? = null,
    @ColumnInfo(name = "submissionDate") val submissionDate: String = "00-00-0000",
    @ColumnInfo(name = "submissionISODate") val submissionISODate: String = "0000-00-00",
    @ColumnInfo(name = "priority") val priority: String? = null,
    @ColumnInfo(name = "taskDetails") val taskDetails: String? = null,

    @ColumnInfo(name = "isComplete")
    @get:PropertyName("isComplete")
    @set:PropertyName("isComplete")
    var isComplete: Boolean = false
) : Serializable {

    @Ignore
    var firebaseId: String = ""

    @Ignore
    constructor(
        id: Int?,
        firebaseId: String,
        taskName: String?,
        submissionDate: String,
        submissionISODate: String,
        priority: String?,
        taskDetails: String?,
        isComplete: Boolean
    ) : this(id, taskName, submissionDate, submissionISODate, priority, taskDetails, isComplete) {
        this.firebaseId = firebaseId
    }
}
