package Adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.collegetracker.R
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView

class customSpinnerAdapter(context: Context, items: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        textView.setTextColor(context.resources.getColor(R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F) // Set text size for dropdown
        textView.setTextColor(context.resources.getColor(R.color.black))
        view.setBackgroundColor(context.resources.getColor(R.color.white))

        return view
    }
}
