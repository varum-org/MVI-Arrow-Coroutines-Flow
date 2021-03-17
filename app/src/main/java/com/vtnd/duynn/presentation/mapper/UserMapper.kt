package com.vtnd.duynn.presentation.mapper

import com.vtnd.duynn.data.model.UserData
import com.vtnd.duynn.domain.entity.User

/**
 * Created by duynn100198 on 3/17/21.
 */
class UserMapper : BaseMapper<UserData, User>() {
    override fun map(data: UserData): User {
        return data.run {
            User(
                userName = userName,
                email = email,
                roleId = roleId,
                imagePath = imagePath,
                id = id,
                phone = phone,
                date = date
            )
        }
    }
}
