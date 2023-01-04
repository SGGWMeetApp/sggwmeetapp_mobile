package pl.sggw.sggwmeet.model.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.domain.PlaceEvent
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.mapper.EventMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.EventConnector
import pl.sggw.sggwmeet.model.connector.dto.request.EventCreatePublicRequest
import pl.sggw.sggwmeet.model.connector.dto.request.EventEditRequest
import pl.sggw.sggwmeet.model.connector.dto.response.EventAddUserResponse
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData
import pl.sggw.sggwmeet.util.Resource
import retrofit2.Response

class EventRepository(
    private val connector: EventConnector,
    private val mapper: EventMapper,
    private val userDataStore: UserDataStore
) {

    companion object {
        private const val TAG = "EventRepository"
    }

    suspend fun getAllEvents() : Flow<Resource<ArrayList<EventResponse>>> = flow {
        val functionTAG = "getAllEvents"
        emit(Resource.Loading())
        try {
            val response = connector.getAllEvents()
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received events")
                emit(Resource.Success(responseBody!!.events))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun getUpcomingEvents() : Flow<Resource<ArrayList<EventResponse>>> = flow {
        val functionTAG = "getUpcomingEvents"
        emit(Resource.Loading())
        try {
            val response = connector.getUpcomingEvents()
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received events")
                emit(Resource.Success(responseBody!!.events))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun getPlacePublicEvents(placeId: String) : Flow<Resource<List<PlaceEvent>>> = flow {
        emit(Resource.Loading())
        try {
            val response = connector.getPlacePublicEvents(placeId)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received place public events : $responseBody")
                val mappedEvents = mapper.mapPlaceEvents(responseBody!!)
                emit(Resource.Success(mappedEvents))
            }
            else{
                Log.e(TAG, "An exception occurred during fetching place public events : ${response.errorBody()}")
                emit(Resource.Error(ServerException(response.code().toString(), response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during fetching place public events", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during fetching place public events", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during fetching place public events", exception)
            emit(Resource.Error(TechnicalException("An error occurred during fetching place public events")))
        }
    }


    suspend fun getAllPlaces() : Flow<Resource<List<SimplePlaceResponseData>>> = flow {
        val functionTAG = "getAllPlaces"
        emit(Resource.Loading())
        try {
            val response = connector.getAllPlaces()
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received places")
                emit(Resource.Success(responseBody!!.places))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun editEvent(eventEditRequest: EventEditRequest, eventId: Int) : Flow<Resource<EventResponse>> = flow {
        val functionTAG = "editEvent"
        emit(Resource.Loading())
        try {
            val response = connector.editEvent(eventEditRequest, eventId)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Edited event")
                emit(Resource.Success(responseBody))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun createPublicEvent(eventCreatePublicRequest: EventCreatePublicRequest) : Flow<Resource<EventResponse>> = flow {
        val functionTAG = "createPublicEvent"
        emit(Resource.Loading())
        try {
            val response = connector.createPublicEvent(eventCreatePublicRequest)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Created event")
                emit(Resource.Success(responseBody))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun createGroupEvent(eventCreateRequest: EventCreatePublicRequest, group_id: Int) : Flow<Resource<EventResponse>> = flow {
        val functionTAG = "createGroupEvent"
        emit(Resource.Loading())
        try {
            val response = connector.createGroupEvent(eventCreateRequest, group_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Created group event")
                emit(Resource.Success(responseBody))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun addUserToEvent(event_id: Int, user_id: Int) : Flow<Resource<EventAddUserResponse>> = flow {
        val functionTAG = "addUserToEvent"
        emit(Resource.Loading())
        try {
            val response = connector.addUserToEvent(event_id, user_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Added user to event")
                emit(Resource.Success(responseBody))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun joinPublicEvent(eventId: Int) : Flow<Resource<Void>> = flow {
        emit(Resource.Loading())
        try {
            val userId = userDataStore.getUserId()
            val response = connector.addUserToEvent(eventId, userId)
            if(response.isSuccessful) {
                Log.i(TAG, "User joined event successfully")
                emit(Resource.Success())
            }
            else {
                Log.e(TAG, "An exception occurred during joining public event, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during during joining public event", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during during joining public event", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during during joining public event", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun leavePublicEvent(eventId: Int) : Flow<Resource<Void>> = flow {
        emit(Resource.Loading())
        try {
            val userId = userDataStore.getUserId()
            val response = connector.deleteUserFromEvent(eventId, userId)
            if(response.isSuccessful) {
                Log.i(TAG, "User leaved event successfully")
                emit(Resource.Success())
            }
            else {
                Log.e(TAG, "An exception occurred during leaving public event, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during during leaving public event", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during during leaving public event", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during during leaving public event", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun deleteUserFromEvent(event_id: Int, user_id: Int) : Flow<Resource<String>> = flow {
        val functionTAG = "deleteUserFromEvent"
        emit(Resource.Loading())
        try {
            val response = connector.deleteUserFromEvent(event_id, user_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Deleted user from event")
                emit(Resource.Success(responseBody))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun getUserEvents(user_id: Int) : Flow<Resource<ArrayList<EventResponse>>> = flow {
        val functionTAG = "getUserEvents"
        emit(Resource.Loading())
        try {
            val response = connector.getUserEvents(user_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received user events")
                emit(Resource.Success(responseBody!!.events))
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun deleteEvent(eventId: Int) : Flow<Resource<Void>> = flow {
        val functionTAG = "deleteEvent"
        emit(Resource.Loading())
        try {
            val response = connector.deleteEvent(eventId)
            if(response.isSuccessful) {
                Log.i(TAG, "Deleted event")
                emit(Resource.Success())
            }
            else{
                Log.e(TAG, "An exception occurred during $functionTAG, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during $functionTAG", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during $functionTAG", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }
}