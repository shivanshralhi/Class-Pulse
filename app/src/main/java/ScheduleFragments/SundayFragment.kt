package ScheduleFragments

import Activities.Daily_Schedule
import Activities.FragmentArrays
import Activities.sharedPreferenceManager
import Database.DatabaseHelper
import Adapters.RecyclerScheduleAdapter
import Adapters.ScheduleItemClickListener
import Models.ScheduleModel
import Database.ScheduleDatabaseHelper
import Models.ScheduleDataclass
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collegetracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SundayFragment : Fragment(), ScheduleItemClickListener {
    private var arrScheduleSunday = ArrayList<ScheduleModel>()
    private lateinit var scheduleAdapter: RecyclerScheduleAdapter
    private lateinit var database: ScheduleDatabaseHelper
    private lateinit var attDatabase: DatabaseHelper //Attendance Database for AutoCompleteTextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sunday, container, false)

        // Initialization of ScheduleDatabase
        database= ScheduleDatabaseHelper.getDB(context)!!

        //Initialization of Attendance RoomDatabase for AutoCompleteTextView
        attDatabase= DatabaseHelper.getDB(context)!!

       // val arrScheduleSunday = ArrayList<ScheduleModel>()
        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator

        arrScheduleSunday = FragmentArrays.arrScheduleSunday

        val sundayRecyclerView = view.findViewById<RecyclerView>(R.id.SundayRecyclerView)
        scheduleAdapter = RecyclerScheduleAdapter(requireContext(), this, arrScheduleSunday)
        scheduleAdapter.notifyDataSetChanged()
        sundayRecyclerView.adapter = scheduleAdapter
        sundayRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        floatingActionButton.setOnClickListener {
            vibrator.vibrate(50)
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.add_update_schedule)

            val addLecture = dialog.findViewById<AutoCompleteTextView>(R.id.addLecture)
            val addTime = dialog.findViewById<EditText>(R.id.addTime)
            val addSchedule = dialog.findViewById<ImageView>(R.id.addSchedule)
            val deleteSchedule = dialog.findViewById<ImageView>(R.id.deleteSchedule)

            // Creating an array for AutoCompleteTextView
            lifecycleScope.launch {
                val arrSubjectNames=attDatabase.attendanceDao().getAllSubjectNames()
                val actvAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrSubjectNames )
                addLecture.setAdapter(actvAdapter)
                addLecture.threshold=1
            }

            addSchedule.setOnClickListener {
                vibrator.vibrate(50)
                var lectureName = ""
                var timeName = ""

                lectureName = addLecture.text.toString()
                timeName = addTime.text.toString()

                val sharedPreferenceManager= sharedPreferenceManager(requireContext())
                var scheduleID=sharedPreferenceManager.getScheduleID()

                if (lectureName != "") {
                    arrScheduleSunday.add(ScheduleModel(scheduleID, "sunday", lectureName, timeName))
                    scheduleAdapter.notifyItemChanged(arrScheduleSunday.size - 1)
                    sundayRecyclerView.scrollToPosition(arrScheduleSunday.size - 1)

                    // INSERT DATA INTO THE DATABASE
                    GlobalScope.launch {
                        database.scheduleDao().insertSchedule(
                            ScheduleDataclass(
                                scheduleID,
                                "sunday",
                                lectureName,
                                timeName
                            )
                        )
                    }

                    scheduleID++
                    sharedPreferenceManager.updateScheduleID(scheduleID)

                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Lecture Can't be Empty", Toast.LENGTH_SHORT).show()
                }
            }

            deleteSchedule.setOnClickListener {
                vibrator.vibrate(50)
                Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.show()
        }

        return view
    }

    override fun onEditScheduleClicked(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_update_schedule)

        val addLecture = dialog.findViewById<AutoCompleteTextView>(R.id.addLecture)
        val addTime = dialog.findViewById<EditText>(R.id.addTime)
        val addSchedule = dialog.findViewById<ImageView>(R.id.addSchedule)
        val deleteSchedule = dialog.findViewById<ImageView>(R.id.deleteSchedule)
        val id = arrScheduleSunday[position].subjectId
        val vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(50)

        addLecture.setText(arrScheduleSunday[position].subject)
        addTime.setText(arrScheduleSunday[position].time)

        // Creating an array for AutoCompleteTextView
        lifecycleScope.launch {
            val arrSubjectNames=attDatabase.attendanceDao().getAllSubjectNames()
            val actvAdapter=ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrSubjectNames )
            addLecture.setAdapter(actvAdapter)
            addLecture.threshold=1
        }


        //  Toast.makeText(requireContext(), position.toString() + arrScheduleSunday[position].subject.toString(), Toast.LENGTH_SHORT).show()

        addSchedule.setOnClickListener {
            vibrator.vibrate(50)
            var lectureName = ""
            var timeName = ""

            lectureName = addLecture.text.toString()
            timeName = addTime.text.toString()

            if(id!=0){
                if (lectureName != "") {
                    arrScheduleSunday.set(position, ScheduleModel(id, "sunday", lectureName, timeName))
                    scheduleAdapter.notifyItemChanged(position)

                    // UPDATING DATABASE
                    var dataToUpdate: ScheduleDataclass = ScheduleDataclass(id, "sunday",lectureName,timeName)
                    lifecycleScope.launch(Dispatchers.IO) {
                        database.scheduleDao().updateSchedule(dataToUpdate)
                    }

                    dialog.dismiss()

                } else {
                    Toast.makeText(context, "Lecture Can't be Empty", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(context, "Please exit Schedule page once before updating this Lecture", Toast.LENGTH_SHORT).show()
            }


        }

        deleteSchedule.setOnClickListener(View.OnClickListener {
            vibrator.vibrate(50)

            val builder = AlertDialog.Builder(context)
                .setTitle("Delete Lecture")
                .setIcon(R.drawable.baseline_delete_24)
                .setMessage("Do you want to Delete this Lecture ?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    try {

                        vibrator.vibrate(50)

                        val subjectId=arrScheduleSunday[position].subjectId
                        //DELETING FROM DATABASE
                        GlobalScope.launch {
                            database.scheduleDao().deleteSchedule(subjectId)
                        }

                        arrScheduleSunday.removeAt(position)
                        scheduleAdapter.notifyItemRemoved(position)
                        scheduleAdapter.notifyItemRangeChanged(position, arrScheduleSunday.size - position)
                        dialog.dismiss()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
                .setNegativeButton(
                    "No"
                ) { dialogInterface, i ->
                    vibrator.vibrate(50)
                    Toast.makeText(context, "Deletion Cancelled", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            builder.show()
        })

        dialog.show()
    }

}
