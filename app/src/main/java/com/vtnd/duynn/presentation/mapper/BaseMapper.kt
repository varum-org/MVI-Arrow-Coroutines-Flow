package com.vtnd.duynn.presentation.mapper

import com.vtnd.duynn.data.model.BaseData
import com.vtnd.duynn.domain.entity.BaseEntity

/**
 * Created by duynn100198 on 3/17/21.
 */
abstract class BaseMapper<in T : BaseData, R : BaseEntity> {

    abstract fun map(data: T): R

    open fun nullableMap(entity: T?): R? {
        return entity?.let { map(it) }
    }

    open fun map(dataCollection: Collection<T>): List<R> {
        return dataCollection.map { map(it) }
    }

    open fun nullableMap(dataCollection: Collection<T>?): List<R>? {
        return dataCollection?.map { map(it) }
    }
}
