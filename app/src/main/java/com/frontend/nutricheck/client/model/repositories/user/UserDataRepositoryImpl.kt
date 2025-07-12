package com.frontend.nutricheck.client.model.repositories.user

import android.content.Context
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.DatabaseProvider
import kotlinx.coroutines.flow.Flow

class UserDataRepositoryImpl(context: Context): UserDataRepository {
    private val userDataDao = DatabaseProvider.getDatabase(context).userDataDao()
    private val weightDao = DatabaseProvider.getDatabase(context).weightDao()

    override suspend fun getUserData(): List<UserData> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeightHistory(): List<Weight> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWeights(): Flow<List<Weight>> = weightDao.getAllWeights()
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