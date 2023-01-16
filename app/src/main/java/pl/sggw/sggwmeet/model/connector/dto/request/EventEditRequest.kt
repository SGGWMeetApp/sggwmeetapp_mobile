package pl.sggw.sggwmeet.model.connector.dto.request

data class EventEditRequest(
    var name:String,
    var locationId:Int,
    var description:String?,
    var startDate:String
)
