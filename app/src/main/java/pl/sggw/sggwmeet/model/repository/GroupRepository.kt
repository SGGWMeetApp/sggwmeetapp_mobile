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
import pl.sggw.sggwmeet.model.connector.dto.request.GroupCreateRequest
import pl.sggw.sggwmeet.model.connector.dto.response.GetGroupsResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GroupCreateResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GroupResponse
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
}