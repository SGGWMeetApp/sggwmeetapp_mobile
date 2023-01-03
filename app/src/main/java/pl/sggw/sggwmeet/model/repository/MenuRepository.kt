package pl.sggw.sggwmeet.model.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.domain.FoodMenu
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.mapper.MenuMapper
import pl.sggw.sggwmeet.model.connector.MenuConnector
import pl.sggw.sggwmeet.util.Resource

class MenuRepository(
    private val connector: MenuConnector,
    private val menuMapper: MenuMapper
) {

    companion object {
        private const val TAG = "MenuRepository"
    }

    suspend fun getFoodMenu(menuPath: String) : Flow<Resource<FoodMenu>> = flow {
        emit(Resource.Loading())
        try {
            val response = connector.getPlaceMenu(menuPath)
            if(response.isSuccessful) {
                Log.i(TAG, "Getting place food menu was successful, menu : ${response.body()}")
                val mappedResult = menuMapper.mapToDomain(response.body()!!)
                emit(Resource.Success(mappedResult))
            } else {
                throw java.lang.RuntimeException("Unexpected error : ${response.code()} ${response.errorBody()}")
            }
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred during getting place food menu")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during getting place food menu", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during getting place food menu")))
        }
    }
}