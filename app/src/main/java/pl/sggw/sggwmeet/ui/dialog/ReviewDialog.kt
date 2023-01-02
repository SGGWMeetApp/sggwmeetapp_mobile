package pl.sggw.sggwmeet.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.Review

class ReviewDialog(activity: Activity, review: Review?) {

    constructor(activity: Activity) : this(activity, null)

    private val builder = AlertDialog.Builder(activity)
    private val comment : EditText
    private val positiveRB : RadioButton
    private val negativeRB : RadioButton
    private val addBT : Button
    private val backBT : Button
    private val dialogActions = DialogActions()

    private var alertDialog: AlertDialog? = null
    private var addBTAction : (actions : DialogActions, comment : EditText) -> Unit = {_,_-> }
    private var backBTAction : (actions : DialogActions) -> Unit = { }

    init {
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_review, null)

        addBT = view.findViewById(R.id.add_BT)
        backBT = view.findViewById(R.id.back_BT)
        positiveRB = view.findViewById(R.id.positive_RB)
        negativeRB = view.findViewById(R.id.negative_RB)
        comment = view.findViewById(R.id.comment_ET)
        builder.setView(view)

        addBT.setOnClickListener { addBTAction(dialogActions, comment) }
        backBT.setOnClickListener { backBTAction(dialogActions) }

        if(review != null) {
            injectReviewData(review)
        }
    }

    fun startAlertDialog() {
        alertDialog = builder.create()
        alertDialog?.show()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun onAddButtonClick(action : (actions : DialogActions, comment : EditText) -> Unit ) : ReviewDialog {
        addBTAction = action
        return this
    }

    fun onBackButtonClick(action : (actions : DialogActions) -> Unit ) : ReviewDialog {
        backBTAction = action
        return this
    }

    private fun injectReviewData(review: Review) {
        comment.setText(review.comment)
        negativeRB.isChecked = !review.isPositive
    }

    inner class DialogActions {

        fun getReviewData() : ReviewData {
            return ReviewData(
                comment.text.toString(),
                positiveRB.isChecked
            )
        }

        fun dismissAlertDialog() {
            alertDialog?.dismiss()
        }

        inner class ReviewData(
            val comment: String,
            val isPositive: Boolean
        )
    }
}