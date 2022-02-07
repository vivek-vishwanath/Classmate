package com.example.classmate.fragments.messages.menu

import com.example.classmate.fragments.messages.main.Forum.Companion.from
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import com.example.classmate.R
import com.google.firebase.firestore.DocumentSnapshot
import android.app.DatePickerDialog.OnDateSetListener
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.classmate.Print
import com.example.classmate.fragments.messages.main.Forum
import java.util.*

class NewEventActivity : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null

    var nameET: EditText? = null; var descriptionET: EditText? = null; var locationET: EditText? = null
    var fromDateTV: TextView? = null; var fromTimeTV: TextView? = null
    var toDateTV: TextView? = null; var toTimeTV: TextView? = null
    var createButton: Button? = null

    var preferences: SharedPreferences? = null

    var event: Event? = null
    var from: Date = Date(); var to: Date = from

    var forumID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        forumID = intent.getStringExtra("forumID")
        from = roundUp(from)
        to = roundUp(from)

        preferences =  getSharedPreferences("com.example.classmate.fragments.messages.menu", Context.MODE_PRIVATE)

        firebase()
        setResourceObjects()
        setListeners()
        setTextViews()
    }

    private fun firebase() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun setResourceObjects() {
        nameET = findViewById(R.id.new_event_name_edit_text)
        descriptionET = findViewById(R.id.new_event_description)
        fromDateTV = findViewById(R.id.from_date_text_view)
        fromTimeTV = findViewById(R.id.from_time_text_view)
        toDateTV = findViewById(R.id.to_date_text_view)
        toTimeTV = findViewById(R.id.to_time_text_view)
        locationET = findViewById(R.id.new_event_location)
        createButton = findViewById(R.id.new_event_create_button)
    }

    private fun setListeners() {
        createButton!!.setOnClickListener { createEvent() }
        fromDateTV!!.setOnClickListener { view: View -> datePick(view) }
        toDateTV!!.setOnClickListener { view: View -> datePick(view) }
        fromTimeTV!!.setOnClickListener { view: View -> timePick(view) }
        toTimeTV!!.setOnClickListener { view: View ->timePick(view) }
    }

    private fun setTextViews() {
        fromDateTV!!.text = getDate(from)
        fromTimeTV!!.text = getTime(from)
        toDateTV!!.text = getDate(to)
        toTimeTV!!.text = getTime(to)
    }

    private fun datePick(view: View) {
        val fragment = DatePickerFragment(if (view === fromDateTV) from else to, this)
        fragment.show(supportFragmentManager, "datePicker")
    }

    private fun timePick(view: View) {
        val fragment = TimePickerFragment(if (view === fromTimeTV) from else to, this)
        fragment.show(supportFragmentManager, "datePicker")
    }

    private fun createEvent() {
        val name = nameET!!.text.toString()
        val description = descriptionET!!.text.toString()
        val location = locationET!!.text.toString()
        event = Event(name, forumID!!, description, location, from, to)
        firestore!!.collection("forums").document(forumID!!).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot -> this.onSuccess(snapshot) }
    }

    private fun onSuccess(snapshot: DocumentSnapshot) {
        if (snapshot.data == null) return
        val forum = from(snapshot.data!!)
        val events = forum.events
        events.add(event!!)
        firestore!!.collection("forums").document(forumID!!).update(getMap(forum))
        preferences!!.edit().putString("created event", event!!.serialize()).apply()
        setResult(1)
        finish()
    }

    private fun getMap(forum: Forum): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["name"] = forum.name
        map["id"] = forum.id
        map["description"] = forum.description
        map["privacy"] = forum.privacy
        map["users"] = forum.users
        map["key"] = forum.key
        map["events"] = forum.events
        return map
    }

    override fun onBackPressed() {
        setResult(-1)
        super.onBackPressed()
    }

    internal class DatePickerFragment(var date: Date, var activity: NewEventActivity) :
        DialogFragment(), OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return DatePickerDialog(getActivity()!!, this, date.year + 1900, date.month, date.date)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            date.year = year - 1900
            date.month = month
            date.date = day
            activity.setTextViews()
        }
    }

    internal class TimePickerFragment(var date: Date, var activity: NewEventActivity) :
        DialogFragment(), TimePickerDialog.OnTimeSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return TimePickerDialog(context!!, this, date.hours, date.minutes, false)
        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            date.hours = hourOfDay
            date.minutes = minute
            activity.setTextViews()
        }
    }

    companion object {

        fun roundUp(date: Date?): Date {
            date!!.time = date.time + 3600000
            date.minutes = 0
            date.seconds = 0
            return date
        }

        fun getDate(date: Date?): String {
            var s = "${getDay(date!!.day)}  ${date.month + 1}/${date.date}"
            if (date.year != Date().year) s += "/${date.year % 100}"
            return s
        }

        fun getTime(date: Date?): String {
            val hours = date!!.hours.toString()
            var minutes = date.minutes.toString()
            if (date.minutes < 10) minutes = "0$minutes"
            return "$hours:$minutes"
        }

        private fun getDay(int: Int): String {
            when(int) {
                0 -> return "Sun"
                1 -> return "Mon"
                2 -> return "Tue"
                3 -> return "Wed"
                4 -> return "Thu"
                5 -> return "Fri"
                6 -> return "Sat"
            }
            return ""
        }
    }
}