class MealService(
    private val mealRepository: IMealRepository
) : IMealService {
    override suspend fun addMeal(meal: Meal) {
        mealRepository.addMeal(meal)
    }

    override suspend fun removeMeal(mealId: FoodComponentId) {
        mealRepository.removeMeal(mealId)
    }

    override suspend fun getAllMeals(): List<Meal> {
        return mealRepository.getAllMeals()
    }
}