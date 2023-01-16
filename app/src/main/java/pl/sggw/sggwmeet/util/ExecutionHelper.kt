package pl.sggw.sggwmeet.util

import pl.sggw.sggwmeet.BuildConfig

class ExecutionHelper {
    companion object {

        private const val MOCKED_BUILD_TYPE = "mocked"

        fun isRunningInMockMode() : Boolean {
            return BuildConfig.BUILD_TYPE == MOCKED_BUILD_TYPE
        }
    }
}