package pl.sggw.sggwmeet.model.repository

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.mapper.GroupMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.GroupConnector
import pl.sggw.sggwmeet.model.connector.dto.request.GroupAddUserRequest
import pl.sggw.sggwmeet.model.connector.dto.request.GroupCreateRequest
import pl.sggw.sggwmeet.model.connector.dto.request.GroupEventNotificationRequest
import pl.sggw.sggwmeet.model.connector.dto.response.*
import pl.sggw.sggwmeet.util.Resource
import java.util.*
import kotlin.Comparator

class GroupRepository(
    private val connector: GroupConnector,
    private val mapper: GroupMapper,
    private val userDataStore: UserDataStore
) {

    companion object {
        private const val TAG = "GroupRepository"
    }

    suspend fun getAllGroups() : Flow<Resource<GetGroupsResponse>> = flow {
        val functionTAG = "getAllGroups"
        emit(Resource.Loading())
        try {
            val response = connector.getAllGroups()
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received all groups")
                Collections.sort(responseBody!!.groups,NameComparator())
                emit(Resource.Success(responseBody!!))
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

    suspend fun getUserGroups(user_id:Int) : Flow<Resource<GetGroupsResponse>> = flow {
        val functionTAG = "getUserGroups"
        emit(Resource.Loading())
        try {
            val response = connector.getUserGroups(user_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Collections.sort(responseBody!!.groups,NameComparator())
                Log.i(TAG, "Received user groups")
                emit(Resource.Success(responseBody!!))
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

    private class NameComparator : Comparator<GroupResponse> {
        override fun compare(g1: GroupResponse, g2: GroupResponse): Int {
            return g1.name.lowercase().compareTo(g2.name.lowercase())
        }
    }

    suspend fun createNewGroup(groupCreateRequest: GroupCreateRequest) : Flow<Resource<GroupCreateResponse>> = flow {
        val functionTAG = "createNewGroup"
        emit(Resource.Loading())
        try {
            val response = connector.createNewGroup(groupCreateRequest)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Created new group")
                emit(Resource.Success(responseBody!!))
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

    suspend fun addUserToGroup(addUserRequest: GroupAddUserRequest, group_id: Int) : Flow<Resource<GroupAddUserResponse>> = flow {
        val functionTAG = "addUserToGroup"
        emit(Resource.Loading())
        try {
            val response = connector.addUserToGroup(addUserRequest, group_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Added user to group")
                emit(Resource.Success(responseBody!!))
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

    suspend fun getGroupMembers(group_id: Int) : Flow<Resource<GroupGetMembersResponse>> = flow {
        val functionTAG = "getGroupMembers"
        emit(Resource.Loading())
        try {
            val response = connector.getGroupMembers(group_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received group members")
                emit(Resource.Success(responseBody!!))
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

    suspend fun getGroupEvents(group_id: Int) : Flow<Resource<GetEventResponse>> = flow {
        val functionTAG = "getGroupEvents"
        emit(Resource.Loading())
        try {
            val response = connector.getGroupEvents(group_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Received group events")
                emit(Resource.Success(responseBody!!))
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

    suspend fun switchGroupNotification(notificationRequest: GroupEventNotificationRequest, group_id: Int, event_id: Int) : Flow<Resource<EventResponse>> = flow {
        val functionTAG = "switchGroupNotification"
        emit(Resource.Loading())
        try {
            val response = connector.switchGroupEventNotification(notificationRequest, group_id, event_id)
            if(response.isSuccessful) {
                val responseBody = response.body()
                Log.i(TAG, "Switched group event notification settings")
                emit(Resource.Success(responseBody!!))
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

    suspend fun deleteGroupEvent(group_id: Int, event_id: Int) : Flow<Resource<String>> = flow {
        val functionTAG = "deleteGroupEvent"
        emit(Resource.Loading())
        try {
            val response = connector.deleteGroupEvent(group_id, event_id)
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

    suspend fun deleteGroup(group_id: Int) : Flow<Resource<String>> = flow {
        val functionTAG = "deleteGroup"
        emit(Resource.Loading())
        try {
            val response = connector.deleteGroup(group_id)
            if(response.isSuccessful) {
                Log.i(TAG, "Deleted group")
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

    suspend fun deleteUser(group_id: Int, user_id: Int) : Flow<Resource<GetGroupsResponse>> = flow {
        val functionTAG = "deleteUser"
        emit(Resource.Loading())
        try {
            val response = connector.deleteUser(group_id, user_id)
            if(response.isSuccessful) {
                Log.i(TAG, "Deleted group")
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