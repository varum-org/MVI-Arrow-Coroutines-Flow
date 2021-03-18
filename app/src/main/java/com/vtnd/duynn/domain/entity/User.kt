package com.vtnd.duynn.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Created by duynn100198 on 3/17/21.
 */
@Parcelize
data class User(
    val userName: String? = null,
    val email: String?,
    val roleId: Int? = null,
    val imagePath: String? = null,
    val id: String?,
    val phone: String? = null,
    val date: Date? = null
) : Parcelable, BaseEntity()
