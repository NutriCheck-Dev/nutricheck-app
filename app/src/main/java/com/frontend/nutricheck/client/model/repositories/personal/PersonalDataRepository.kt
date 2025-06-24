package com.frontend.nutricheck.client.model.repositories.personal

import com.frontend.nutricheck.client.model.data_layer.UserData

class PersonalDataRepository: BasePersonalDataRepository {
    override suspend fun load(): UserData {
        TODO("Not yet implemented")
    }

    override suspend fun save(userData: UserData) {
        TODO("Not yet implemented")
    }

    override suspend fun update(userData: UserData) {
        TODO("Not yet implemented")
    }
}