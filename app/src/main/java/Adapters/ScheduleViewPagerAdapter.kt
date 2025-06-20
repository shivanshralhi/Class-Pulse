package Adapters

import ScheduleFragments.FridayFragment
import ScheduleFragments.MondayFragment
import ScheduleFragments.SaturdayFragment
import ScheduleFragments.SundayFragment
import ScheduleFragments.ThursdayFragment
import ScheduleFragments.TuesdayFragment
import ScheduleFragments.WednesdayFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class scheduleViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle ): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> MondayFragment()
            1 -> TuesdayFragment()
            2 -> WednesdayFragment()
            3 -> ThursdayFragment()
            4 -> FridayFragment()
            5 -> SaturdayFragment()
            6 -> SundayFragment()
            else -> MondayFragment() // Default case, though it should never happen

        }
    }

}