package Database

import Models.Attendance
import Models.TaskDataClass
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities =[Attendance::class, TaskDataClass::class], version = 2)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDAO
    abstract fun taskDao(): TaskDAO

    companion object {


        const val DB_NAME = "CollegeTracker_DB"
        var instance: DatabaseHelper? = null
        @Synchronized
        fun getDB(context: Context?): DatabaseHelper? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context!!.applicationContext,
                    DatabaseHelper::class.java, DB_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE task_table ADD COLUMN submissionDate TEXT DEFAULT '00-00-0000' NOT NULL")
                database.execSQL("ALTER TABLE task_table ADD COLUMN submissionISODate TEXT DEFAULT '0000-00-00' NOT NULL")
            }
        }
    }
}