package pl.sggw.sggwmeet.model.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.mapper.EventMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.EventConnector
import pl.sggw.sggwmeet.model.connector.dto.request.EventCreatePublicRequest
import pl.sggw.sggwmeet.model.connector.dto.request.EventEditRequest
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData
import pl.sggw.sggwmeet.util.Resource

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
}