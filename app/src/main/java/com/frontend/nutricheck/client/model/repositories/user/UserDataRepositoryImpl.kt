package com.frontend.nutricheck.client.model.repositories.user

import android.content.Context
import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.model.data_layer.Weight
import com.frontend.nutricheck.client.model.persistence.DatabaseProvider

class UserDataRepositoryImpl(context: Context): UserDataRepository {
    private val userDataDao = DatabaseProvider.getDatabase(context).userDataDao()
    private val weightDao = DatabaseProvider.getDatabase(context).weightDao()

    override suspend fun getUserData(): List<UserData> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeightHistory(): List<Weight> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWeights(): List<Weight>? = weightDao.getAllWeights()
    override suspend fun addWeight(weight: Weight) {
        TODO("Not yet implemented")
    }

    override suspend fun addUserData(userData: UserData) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserData(userData: UserData) {
        TODO("Not yet implemented")
    }

}