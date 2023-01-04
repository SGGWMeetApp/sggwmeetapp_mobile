package pl.sggw.sggwmeet.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.PlaceEvent
import java.text.SimpleDateFormat
import java.util.*

class AddPublicEventDialog(activity: Activity, event: PlaceEvent?) {

    companion object {
        private const val CREATE_EVENT_TEXT = "Utwórz wydarzenie"
        private const val EDIT_EVENT_TEXT = "Edytuj wydarzenie"
        private val DATE_PATTERN = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US)
    }

    constructor(activity: Activity) : this(activity, null)

    private val builder = AlertDialog.Builder(activity)
    private val headerTV : TextView
    private val eventNameTIET : TextInputEditText
    private val eventDescriptionTIET : TextInputEditText
    private val eventStartDateTV: TextView
    private val selectDateBT: Button
    private val selectTimeBT: Button
    private val confirmBT: Button
    private val cancelBT: Button
    private val loadingPB: ProgressBar

    private val dialogActions = DialogActions()

    private var alertDialog: AlertDialog? = null
    private var confirmBTAction : (actions : DialogActions, eventSnapshot: PublicEventSnapshot) -> Unit = { _, _-> }
    private var cancelBTAction : (actions : DialogActions) -> Unit = { }

    private val selectedCalendar = Calendar.getInstance()

    init {
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_public_event, null)

        headerTV = view.findViewById(R.id.header_TV)
        eventNameTIET = view.findViewById(R.id.event_name_TIET)
        eventDescriptionTIET = view.findViewById(R.id.event_description_TIET)
        eventStartDateTV = view.findViewById(R.id.event_start_date_TV)
        selectDateBT = view.findViewById(R.id.select_date_BT)
        selectTimeBT = view.findViewById(R.id.select_time_BT)
        confirmBT = view.findViewById(R.id.confirm_BT)
        cancelBT = view.findViewById(R.id.cancel_BT)
        loadingPB = view.findViewById(R.id.loading_PB)
        builder.setView(view)

        confirmBT.setOnClickListener { confirmBTAction(dialogActions, buildEventSnapshot()) }
        cancelBT.setOnClickListener { cancelBTAction(dialogActions) }

        if(event != null) {
            injectEventData(event)
        }

        if(event != null) {
            headerTV.text = EDIT_EVENT_TEXT
            selectedCalendar.time = event.startDate
        } else {
            headerTV.text = CREATE_EVENT_TEXT
        }

        if(event != null) {
            injectEventData(event)
        }

        selectDateBT.setOnClickListener {
            selectDate(activity)
        }

        selectTimeBT.setOnClickListener {
            selectTime(activity)
        }
    }

    private fun selectDate(activity: Activity) {
        DatePickerDialog(activity, R.style.DatePicker, { _, year, monthOfYear, dayOfMonth ->
            selectedCalendar.set(year, monthOfYear, dayOfMonth)
            eventStartDateTV.text = DATE_PATTERN.format(selectedCalendar.time)
        },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    private fun selectTime(activity: Activity) {
        TimePickerDialog(activity, R.style.DatePicker, { _, hourOfDay, minute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedCalendar.set(Calendar.MINUTE, minute)
            eventStartDateTV.text = DATE_PATTERN.format(selectedCalendar.time)
        }, selectedCalendar.get(Calendar.HOUR_OF_DAY),
            selectedCalendar.get(Calendar.MINUTE),
            true)
            .show()
    }

    private fun injectEventData(event: PlaceEvent) {
        eventNameTIET.setText(event.name)
        eventDescriptionTIET.setText(event.description)
        eventStartDateTV.text = DATE_PATTERN.format(selectedCalendar.time)
    }

    private fun buildEventSnapshot() : PublicEventSnapshot {
        return PublicEventSnapshot(
            eventNameTIET.text.toString(),
            eventDescriptionTIET.text.toString(),
            selectedCalendar.time
        )
    }

    fun startAlertDialog() {
        alertDialog = builder.create()
        alertDialog?.show()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun onConfirmButtonClick(action : (actions : DialogActions, eventSnapshot : PublicEventSnapshot) -> Unit) : AddPublicEventDialog {
        confirmBTAction = action
        return this
    }

    fun onBackButtonClick(action : (actions : DialogActions) -> Unit) : AddPublicEventDialog {
        cancelBTAction = action
        return this
    }

    inner class DialogActions {
        fun dismissAlertDialog() {
            alertDialog?.dismiss()
        }
    }

    data class PublicEventSnapshot(
        val eventName: String,
        val eventDescription: String,
        val eventDate: Date
    ) {}
}