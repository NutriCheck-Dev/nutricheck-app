package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import com.frontend.nutricheck.client.model.data_sources.data.UserData

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