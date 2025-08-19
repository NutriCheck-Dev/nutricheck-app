package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

/**
 * Unit test class for UserDataRepositoryImpl.
 *
 * Tests all repository operations including user data retrieval, weight management,
 * and goal calculations using mocked DAOs to ensure proper business logic implementation.
 */
@ExperimentalCoroutinesApi
class UserDataRepositoryImplTest {

    private val mockWeightDao = mockk<WeightDao>()
    private val mockUserDataDao = mockk<UserDataDao>()

    private lateinit var repository: UserDataRepositoryImpl

    private val sampleUserData = UserData(
        username = "testUser",
        birthdate = Date(631152000000L), // 1990-01-01
        height = 180.0,
        weightGoal = WeightGoal.LOSE_WEIGHT,
        activityLevel = ActivityLevel.NEVER,
        gender = Gender.MALE,
        targetWeight = 70.0,
        dailyCaloriesGoal = 2000,
        proteinGoal = 150,
        carbsGoal = 250,
        fatsGoal = 65
    )

    private val sampleWeight = Weight(
        value = 75.0,
        date = Date(1104537600000L) // 2005-01-01
    )

    private val weightList = listOf(
        Weight(value = 75.0, date = Date(1104537600000L)), // 2005-01-01
        Weight(value = 76.0, date = Date(1199145600000L)), // 2008-01-01
        Weight(
            value = 74.5, date = Date(1420070400000L) // 2015-01-01
        )
    )

    /**
     * Sets up the test environment before each test.
     * Initializes the repository with mocked dependencies.
     */
    @Before
    fun setUp() {
        repository = UserDataRepositoryImpl(mockWeightDao, mockUserDataDao)
    }

    /**
     * Tests getUserData() when user data exists in the database.
     * Verifies that the method returns the correct UserData object.
     */
    @Test
    fun `getUserData returns existing user data when available`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns sampleUserData

        val result = repository.getUserData()

        assertEquals(sampleUserData, result)
        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests getUserData() when no user data exists in the database.
     * Verifies that the method returns a default UserData object.
     */
    @Test
    fun `getUserData returns default UserData when none exists`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns null

        val result = repository.getUserData()

        // Verify that returned UserData has default values
        // Birthdate works with Date() so it is not checked here
        assertEquals("", result.username)
        assertEquals(Gender.DIVERS, result.gender)
        assertEquals(0.0, result.height)
        assertEquals(0.0, result.weight)
        assertEquals(0, result.age)
        assertEquals(0.0, result.targetWeight)
        assertEquals(0, result.dailyCaloriesGoal)
        assertEquals(0, result.proteinGoal)
        assertEquals(0, result.carbsGoal)
        assertEquals(0, result.fatsGoal)
        assertEquals(WeightGoal.MAINTAIN_WEIGHT, result.weightGoal)
        assertEquals(ActivityLevel.NEVER, result.activityLevel)

        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests getWeightHistory() method.
     * Verifies that it returns the complete list of weight entries.
     */
    @Test
    fun `getWeightHistory returns list of weights`() = runTest {
        every { mockWeightDao.getAllWeights() } returns flowOf(weightList)

        val result = repository.getWeightHistory()

        assertEquals(weightList, result)
        coVerify { mockWeightDao.getAllWeights() }
    }

    /**
     * Tests addWeight() method.
     * Verifies that the weight is properly inserted through the DAO.
     */
    @Test
    fun `addWeight inserts weight through dao`() = runTest {
        coEvery { mockWeightDao.insert(sampleWeight) } returns Unit

        repository.addWeight(sampleWeight)

        coVerify { mockWeightDao.insert(sampleWeight) }
    }

    /**
     * Tests addUserData() method.
     * Verifies that user data is properly inserted through the DAO.
     */
    @Test
    fun `addUserData inserts user data through dao`() = runTest {
        coEvery { mockUserDataDao.insert(sampleUserData) } returns Unit

        repository.addUserData(sampleUserData)

        coVerify { mockUserDataDao.insert(sampleUserData) }
    }

    /**
     * Tests updateUserData() method.
     * Verifies that user data is properly updated through the DAO.
     */
    @Test
    fun `updateUserData updates user data through dao`() = runTest {
        coEvery { mockUserDataDao.update(sampleUserData) } returns Unit

        repository.updateUserData(sampleUserData)

        coVerify { mockUserDataDao.update(sampleUserData) }
    }

    /**
     * Tests getTargetWeight() when user data exists.
     * Verifies that the correct target weight is returned.
     */
    @Test
    fun `getTargetWeight returns target weight when user data exists`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns sampleUserData

        val result = repository.getTargetWeight()

        assertEquals(70.0, result)
        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests getTargetWeight() when no user data exists.
     * Verifies that 0.0 is returned as the default value.
     */
    @Test
    fun `getTargetWeight returns 0_0 when no user data exists`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns null

        val result = repository.getTargetWeight()

        assertEquals(0.0, result)
        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests getDailyCalorieGoal() when user data exists.
     * Verifies that the correct daily calorie goal is returned.
     */
    @Test
    fun `getDailyCalorieGoal returns calorie goal when user data exists`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns sampleUserData

        val result = repository.getDailyCalorieGoal()

        assertEquals(2000, result)
        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests getDailyCalorieGoal() when no user data exists.
     * Verifies that 0 is returned as the default value.
     */
    @Test
    fun `getDailyCalorieGoal returns 0 when no user data exists`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns null

        val result = repository.getDailyCalorieGoal()

        assertEquals(0, result)
        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests getNutrientGoal() when user data exists.
     * Verifies that the correct list of nutrient goals (carbs, protein, fats) is returned.
     */
    @Test
    fun `getNutrientGoal returns nutrient goals when user data exists`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns sampleUserData

        val result = repository.getNutrientGoal()

        assertEquals(listOf(250, 150, 65), result)
        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests getNutrientGoal() when no user data exists.
     * Verifies that a list of zeros is returned as the default value.
     */
    @Test
    fun `getNutrientGoal returns zeros when no user data exists`() = runTest {
        coEvery { mockUserDataDao.getUserData() } returns null

        val result = repository.getNutrientGoal()

        assertEquals(listOf(0, 0, 0), result)
        coVerify { mockUserDataDao.getUserData() }
    }

    /**
     * Tests addUserDataAndAddWeight() method.
     * Verifies that both user data and weight are properly inserted through their respective DAOs.
     */
    @Test
    fun `addUserDataAndAddWeight inserts both user data and weight`() = runTest {
        coEvery { mockUserDataDao.insert(sampleUserData) } returns Unit
        coEvery { mockWeightDao.insert(sampleWeight) } returns Unit

        repository.addUserDataAndAddWeight(sampleUserData, sampleWeight)

        coVerify { mockUserDataDao.insert(sampleUserData) }
        coVerify { mockWeightDao.insert(sampleWeight) }
    }
}