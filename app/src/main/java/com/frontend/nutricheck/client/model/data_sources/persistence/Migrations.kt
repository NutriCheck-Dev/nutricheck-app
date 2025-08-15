package com.frontend.nutricheck.client.model.data_sources.persistence

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_15_16 = object: Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
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
}