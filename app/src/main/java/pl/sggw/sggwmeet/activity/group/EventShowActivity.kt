package pl.sggw.sggwmeet.activity.group

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.databinding.ActivityEventShowBinding
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import java.text.SimpleDateFormat

@AndroidEntryPoint
class EventShowActivity: AppCompatActivity() {
    private lateinit var binding : ActivityEventShowBinding
    private lateinit var eventData : EventResponse
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy' 'HH:MM")
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityEventShowBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()

        val retrievedData: String? = intent.getStringExtra("eventData")
        setUpEvent(retrievedData)
    }
    private fun setUpEvent(data:String?){
        if(data.isNullOrBlank()){
            this.finish()
        }
        try {
            eventData=gson.fromJson(data,EventResponse::class.java)
            binding.eventNameTV.setText(eventData.name)
            binding.eventStartDateTV.setText(timeFormat.format(eventData.startDate))
            binding.eventLocationTV.setText(eventData.locationData.name)
            binding.eventDescriptionTV.setText(eventData.description)
            binding.eventAuthorTV.setText("${eventData.author.firstName} ${eventData.author.lastName}")
            binding.eventEmailTV.setText(eventData.author.email)
            val editCheck= Prefs.read().content("email","")==eventData.author.email && eventData.canEdit
            binding.editEventBT.isEnabled=editCheck
            binding.editEventBT.isGone=editCheck.not()
        }
        catch (e:Exception){
            this.finish()
        }
    }
    private fun setUpButtons(){
        binding.navbarActivity.closeBT.setOnClickListener {
            this.finish()
        }
        binding.exitBT.setOnClickListener{
            this.finish()
        }
        binding.editEventBT.setOnClickListener{
            val newActivity = Intent(this, EventEditActivity::class.java)
                .putExtra("eventData",gson.toJson(eventData))
            startActivity(newActivity)
        }
    }
}