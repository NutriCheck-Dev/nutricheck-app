package com.frontend.nutricheck.client.model.persistence.dao

import com.frontend.nutricheck.client.model.data_layer.UserData

interface UserDataDao : BaseDao<UserData> {

    override suspend fun insert(obj: UserData) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: UserData) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: UserData) {
        TODO("Not yet implemented")
    }
}