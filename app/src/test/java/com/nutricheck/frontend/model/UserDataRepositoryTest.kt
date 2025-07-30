package com.nutricheck.frontend.model

import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class UserDataRepositoryImplTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    class MainCoroutineRule : TestWatcher() {
        override fun starting(description: Description?) {
            Dispatchers.setMain(StandardTestDispatcher())
        }
        override fun finished(description: Description?) {
            Dispatchers.resetMain()
        }
    }

    @Mock
    private lateinit var userDataDao: UserDataDao
    @Mock
    private lateinit var weightDao: WeightDao
    private lateinit var repository: UserDataRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = UserDataRepositoryImpl(weightDao, userDataDao)
    }

    @Test
    fun `getUserData returns user data if exists`() = runTest {
        val userData = UserData()
        `when`(userDataDao.getUserData()).thenReturn(userData)
        val result = repository.getUserData()
        assertEquals(userData, result)
    }

    @Test
    fun `getUserData returns default if none exists`() = runTest {
        `when`(userDataDao.getUserData()).thenReturn(null)
        val result = repository.getUserData()
        assertNotNull(result)
    }

    @Test
    fun `getWeightHistory returns all weights`() = runTest {
        val weights = listOf(Weight(70.0), Weight(71.0))
        `when`(weightDao.getAllWeights()).thenReturn(flowOf(weights))
        val result = repository.getWeightHistory()
        assertEquals(weights, result)
    }
    @Test
    fun `addWeight inserts new weight if date not exists`() = runTest {
        val newWeight = Weight(72.0)
        `when`(weightDao.getAllWeights()).thenReturn(flowOf(emptyList()))
        repository.addWeight(newWeight)
        verify(weightDao).insert(newWeight)
    }
    @Test
    fun `addWeight updates weight if date exists`() = runTest {
        val existingWeight = Weight(73.0)
        val newWeight = Weight(74.0, existingWeight.date)
        `when`(weightDao.getAllWeights()).thenReturn(flowOf(listOf(existingWeight)))
        repository.addWeight(newWeight)
        verify(weightDao).update(existingWeight)
    }
    @Test
    fun `addUserData inserts user data`() = runTest {
        val userData = UserData()
        repository.addUserData(userData)
        verify(userDataDao).insert(userData)
    }
    @Test
    fun `updateUserData updates user data`() = runTest {
        val userData = UserData()
        repository.updateUserData(userData)
        verify(userDataDao).update(userData)
    }
    @Test
    fun `getTargetWeight returns value if user data exists`() = runTest {
        val userData = UserData(targetWeight = 80.0)
        `when`(userDataDao.getUserData()).thenReturn(userData)
        val result = repository.getTargetWeight()
        assertEquals(80.0, result, 0.01)
    }
    @Test
    fun `getDailyCalorieGoal returns value if user data exists`() = runTest {
        val userData = UserData(dailyCaloriesGoal = 2000)
        `when`(userDataDao.getUserData()).thenReturn(userData)
        val result = repository.getDailyCalorieGoal()
        assertEquals(2000, result)
    }
    @Test
    fun `getNutrientGoal returns values if user data exists`() = runTest {
        val userData = UserData(carbsGoal = 100, proteinGoal = 50, fatsGoal = 30)
        `when`(userDataDao.getUserData()).thenReturn(userData)
        val result = repository.getNutrientGoal()
        assertEquals(listOf(100, 50, 30), result)
    }
    @Test
    fun `getTargetWeight returns 0_0 if no user data exists`() = runTest {
        `when`(userDataDao.getUserData()).thenReturn(null)
        val result = repository.getTargetWeight()
        assertEquals(0.0, result, 0.01)
    }

    @Test
    fun `getDailyCalorieGoal returns 0 if no user data exists`() = runTest {
        `when`(userDataDao.getUserData()).thenReturn(null)
        val result = repository.getDailyCalorieGoal()
        assertEquals(0, result)
    }

    @Test
    fun `getNutrientGoal returns zeros if no user data exists`() = runTest {
        `when`(userDataDao.getUserData()).thenReturn(null)
        val result = repository.getNutrientGoal()
        assertEquals(listOf(0, 0, 0), result)
    }
}
