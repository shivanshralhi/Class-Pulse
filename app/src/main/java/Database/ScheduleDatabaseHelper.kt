package Database

import Models.ScheduleDataclass
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [ScheduleDataclass::class], version = 1 )
abstract class ScheduleDatabaseHelper: RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDAO

    companion object {

        const val DB_NAME = "ScheduleDB"
        var instance: ScheduleDatabaseHelper? = null
        @Synchronized
        fun getDB(context: Context?): ScheduleDatabaseHelper? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context!!.applicationContext,
                    ScheduleDatabaseHelper::class.java, DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }

    }

}