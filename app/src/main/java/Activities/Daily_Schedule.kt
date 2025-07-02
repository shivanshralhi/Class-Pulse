package Activities

import Adapters.scheduleViewPagerAdapter
import Models.ScheduleModel
import Database.ScheduleDAO
import Database.ScheduleDatabaseHelper
import Models.ScheduleDataclass
import ScheduleFragments.MondayFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.collegetracker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class Daily_Schedule : AppCompatActivity() {

    //var database: ScheduleDatabaseHelper?=null
    private lateinit var scheduleDatabase: ScheduleDatabaseHelper
    private lateinit var scheduleDAO: ScheduleDAO
    var scheduleArray=ArrayList<ScheduleDataclass>()
    private lateinit var sharedPreferenceManager:sharedPreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_schedule2)

        // Database Initialization
        scheduleDatabase= ScheduleDatabaseHelper.getDB(this)!!
        scheduleDAO=scheduleDatabase.scheduleDao()
        scheduleArray = ArrayList(scheduleDAO.getAllSchedule())

        sharedPreferenceManager=sharedPreferenceManager(this)


        lifecycleScope.launch(Dispatchers.IO){
            try {

                FragmentArrays.arrScheduleMonday.clear()
                FragmentArrays.arrScheduleTuesday.clear()
                FragmentArrays.arrScheduleWednesday.clear()
                FragmentArrays.arrScheduleThursday.clear()
                FragmentArrays.arrScheduleFriday.clear()
                FragmentArrays.arrScheduleSaturday.clear()
                FragmentArrays.arrScheduleSunday.clear()

                for (schedule in scheduleArray) {
                    val subjectId = schedule.id
                    val day = schedule.day
                    val lecture = schedule.lecture
                    val time = schedule.time

                    when (day) {
                        "monday" -> FragmentArrays.arrScheduleMonday.add(ScheduleModel(subjectId, day, lecture, time))
                        "tuesday" -> FragmentArrays.arrScheduleTuesday.add(ScheduleModel(subjectId, day, lecture, time))
                        "wednesday" -> FragmentArrays.arrScheduleWednesday.add(ScheduleModel(subjectId, day, lecture, time))
                        "thursday" -> FragmentArrays.arrScheduleThursday.add(ScheduleModel(subjectId, day, lecture, time))
                        "friday" -> FragmentArrays.arrScheduleFriday.add(ScheduleModel(subjectId, day, lecture, time))
                        "saturday" -> FragmentArrays.arrScheduleSaturday.add(ScheduleModel(subjectId, day, lecture, time))
                        "sunday" -> FragmentArrays.arrScheduleSunday.add(ScheduleModel(subjectId, day, lecture, time))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



        val bottomNavigation2=findViewById<BottomNavigationView>(R.id.bottomNavigation2)

        //THE ONLY CODE FOR VIEWPAGER
        val scheduleViewPager: ViewPager2 = findViewById(R.id.scheduleViewPager)
        val adapter = scheduleViewPagerAdapter(supportFragmentManager, lifecycle)
        scheduleViewPager.adapter=adapter

        //OTHER ELEMENTS
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Set the desired offscreen page limit
        val newOffscreenLimit = 7
        scheduleViewPager.offscreenPageLimit = newOffscreenLimit


        // SET THE DEFAULT FRAGMENT TO OPEN BY DEFAULT ACCORDING TO CURRENT DAY
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val defaultDayIndex: Int

        if (currentDay == Calendar.SUNDAY) {
            defaultDayIndex = 6 // Sunday
        } else if (currentDay == Calendar.MONDAY) {
            defaultDayIndex = 0 // Monday
        } else if (currentDay == Calendar.TUESDAY) {
            defaultDayIndex = 1 // Tuesday
        } else if (currentDay == Calendar.WEDNESDAY) {
            defaultDayIndex = 2 // Wednesday
        } else if (currentDay == Calendar.THURSDAY) {
            defaultDayIndex = 3 // Thursday
        } else if (currentDay == Calendar.FRIDAY) {
            defaultDayIndex = 4 // Friday
        } else { // Assuming currentDay == Calendar.SATURDAY
            defaultDayIndex = 5 // Saturday
        }
        scheduleViewPager.currentItem = defaultDayIndex


        //TAB LAYOUT AND SYNCING WITH VIEWPAGER2
        val scheduleTabLayout = findViewById<TabLayout>(R.id.scheduleTabLayout)
        val tabNames = arrayOf("M", "T", "W", "T", "F", "S", "S")
        TabLayoutMediator(scheduleTabLayout, scheduleViewPager) { tab, position ->
            tab.text=tabNames[position]
        }.attach()


        // Set a custom PageTransformer for smooth sliding animation
        scheduleViewPager.setPageTransformer { page, position ->
            val absPosition = Math.abs(position)
            page.alpha = 1f - absPosition
            page.scaleY = 0.85f + 0.15f * (1f - absPosition)
        }







        // Bottom Navigation
        bottomNavigation2.selectedItemId = R.id.Schedule_btmNavigation
        bottomNavigation2.setOnItemSelectedListener {

            when(it.itemId){
                R.id.Attendance_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(1)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                R.id.Tasks_btmNavigation ->{
                    vibrator.vibrate(50)
                    sharedPreferenceManager.updateNavigationCode(3)
                    startActivity(Intent(this, TaskActivity::class.java))
                    finish()
                }
            }

            return@setOnItemSelectedListener true
        }

    }



}
