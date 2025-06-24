package com.frontend.nutricheck.client.model.services

import com.frontend.nutricheck.client.model.data_layer.History
import com.frontend.nutricheck.client.model.repositories.IHistoryRepository

class UserDataService(private val historyRepository: IHistoryRepository) : IUserDataService {
    override suspend fun getUserHistory(): List<History> {
        return historyRepository.getAllHistory()
    }

    override suspend fun clearUserHistory() {
        historyRepository.clearHistory()
    }
}