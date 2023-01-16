package pl.sggw.sggwmeet.util

/**
 * Wrapper for data returned from repository
 * It informs if success or error occurred or long operation is being executed
 */
sealed class Resource<out R> {

    data class Success<out T>(val data: T?): Resource<T>() {
        constructor() : this(null)
    }
    data class Error(val exception: Exception?): Resource<Nothing>() {
        constructor() : this(null)
    }
    data class Loading(val context: Context): Resource<Nothing>() {
        constructor() : this(Context.LOADING)
    }

    enum class Context{
        LOADING, GETTING, INSERTING, DELETING, EDITING
    }
}