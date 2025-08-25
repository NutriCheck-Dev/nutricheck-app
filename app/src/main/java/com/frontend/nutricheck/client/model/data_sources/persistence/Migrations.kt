package com.frontend.nutricheck.client.model.data_sources.persistence

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

object Migrations {
    val MIGRATION_15_16 = object: Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
            //food_search_index migration
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS food_search_index_new(
                query TEXT NOT NULL,
                foodProductId TEXT NOT NULL,
                lastUpdated INTEGER NOT NULL,
                PRIMARY KEY(query, foodProductId),
                FOREIGN KEY(foodProductId) REFERENCES foods(id) 
                ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
                )
            """.trimIndent())

            db.execSQL("DROP TABLE food_search_index")
            db.execSQL("ALTER TABLE food_search_index_new RENAME TO food_search_index")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_food_search_index_foodProductId ON food_search_index(foodProductId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_food_search_index_lastUpdated ON food_search_index(lastUpdated)")

            //recipe_search_index migration
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS recipe_search_index_new(
                query TEXT NOT NULL,
                recipeId TEXT NOT NULL,
                lastUpdated INTEGER NOT NULL,
                PRIMARY KEY(query, recipeId),
                FOREIGN KEY(recipeId) REFERENCES recipes(id) 
                ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
                )
            """.trimIndent())

            db.execSQL("""
                INSERT INTO recipe_search_index_new(query, recipeId, lastUpdated)
                SELECT query, recipeId, lastUpdated FROM recipe_search_index
            """.trimIndent())

            db.execSQL("DROP TABLE recipe_search_index")
            db.execSQL("ALTER TABLE recipe_search_index_new RENAME TO recipe_search_index")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_recipe_search_index_recipeId ON recipe_search_index(recipeId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_recipe_search_index_lastUpdated ON recipe_search_index(lastUpdated)")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_ingredients_foodProductId ON ingredients(foodProductId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_meal_food_items_foodProductId ON meal_food_items(foodProductId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_meal_recipe_items_recipeId ON meal_recipe_items(recipeId)")
        }
    }

    val MIGRATION_16_17 = object : Migration(16, 17) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.beginTransaction()
            try {
                val servingSizeCase = buildServingSizeOrdinalToNameCase()
                //ingredients migration
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS ingredients_new (
                    recipeId TEXT NOT NULL,
                    foodProductId TEXT NOT NULL,
                    quantity REAL NOT NULL,
                    servings REAL NOT NULL,
                    servingSize TEXT NOT NULL,
                    PRIMARY KEY(recipeId, foodProductId),
                    FOREIGN KEY(recipeId) REFERENCES recipes(id)
                    ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,
                    FOREIGN KEY(foodProductId) REFERENCES foods(id)
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO ingredients_new (recipeId, foodProductId, quantity, servings, servingSize)
                    SELECT recipeId, foodProductId, quantity, CAST(servings AS REAL), $servingSizeCase
                    FROM ingredients
                """.trimIndent())

                db.execSQL("DROP TABLE ingredients")
                db.execSQL("ALTER TABLE ingredients_new RENAME TO ingredients")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_ingredients_recipeId ON ingredients(recipeId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_ingredients_foodProductId ON ingredients(foodProductId)")

                //meal_food_items migration
                db.execSQL("""
                CREATE TABLE IF NOT EXISTS meal_food_items_new (
                    mealId TEXT NOT NULL,
                    foodProductId TEXT NOT NULL,
                    quantity REAL NOT NULL,
                    servings REAL NOT NULL,
                    servingSize TEXT NOT NULL,
                    PRIMARY KEY(mealId, foodProductId),
                    FOREIGN KEY(mealId) REFERENCES meals(id)
                    ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,
                    FOREIGN KEY(foodProductId) REFERENCES foods(id)
                )
            """.trimIndent())

                db.execSQL("""
                INSERT INTO meal_food_items_new (mealId, foodProductId, quantity, servings, servingSize)
                SELECT mealId, foodProductId, quantity, CAST(servings AS REAL), $servingSizeCase
                FROM meal_food_items
            """.trimIndent())

                db.execSQL("DROP TABLE meal_food_items")
                db.execSQL("ALTER TABLE meal_food_items_new RENAME TO meal_food_items")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_meal_food_items_mealId ON meal_food_items(mealId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_meal_food_items_foodProductId ON meal_food_items(foodProductId)")

                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
}

private fun buildServingSizeOrdinalToNameCase(): String {
    val fallback = ServingSize.entries.first().name
    val parts = ServingSize.entries.mapIndexed { index, size -> "WHEN $index THEN '${size.name}'" }
    return "CASE servingSize ${parts.joinToString(" ")} ELSE '$fallback' END"
}