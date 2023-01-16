package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.UserChangePasswordData
import pl.sggw.sggwmeet.model.connector.dto.request.UserChangePasswordRequest

class UserMapper {
    fun map(userChangePasswordData: UserChangePasswordData) : UserChangePasswordRequest {
        return UserChangePasswordRequest(
            userChangePasswordData.oldPassword,
            userChangePasswordData.newPassword
        )
    }
}