package pl.sggw.sggwmeet.activity.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.databinding.ActivityGroupShowBinding
import pl.sggw.sggwmeet.model.connector.dto.response.GroupResponse
import java.text.SimpleDateFormat

@AndroidEntryPoint
class GroupShowActivity: AppCompatActivity() {
    private lateinit var binding : ActivityGroupShowBinding
    private lateinit var groupData : GroupResponse
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityGroupShowBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()

        val retrievedData: String? = intent.getStringExtra("groupData")
        setUpEvent(retrievedData)
    }
    private fun setUpEvent(data:String?){
        if(data.isNullOrBlank()){
            this.finish()
        }
        try {
            groupData=gson.fromJson(data,GroupResponse::class.java)
            binding.groupInfoNameTV.setText(groupData.name)
            binding.groupInfoAdminTV.setText("${groupData.adminData.firstName} ${groupData.adminData.lastName}")
            val adminCheck=groupData.adminData.isUserAdmin
            binding.editEventBT.isEnabled=adminCheck
            binding.editEventBT.isGone=adminCheck.not()
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
//        binding.editEventBT.setOnClickListener{
//            val newActivity = Intent(this, EventEditActivity::class.java)
//                .putExtra("groupData",gson.toJson(groupData))
//            startActivityForResult(newActivity,103)
//        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==103 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                setUpEvent(data.getStringExtra("newEventData"))
                this.setResult(Activity.RESULT_OK)
            }

        }
    }
}