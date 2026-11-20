package com.smartwallet.data.database;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.Category;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Budget.class, Category.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BudgetDao budgetDao();
    public abstract CategoryDao categoryDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * ترحيل 1→2: إسقاط الأعمدة الزائدة التي أُنشئت بسبب غياب @Ignore سابقاً
     * (categoryName, categoryIcon, categoryColor) حيث لا تُخزَّن فعلياً
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `budgets_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`type` INTEGER NOT NULL, " +
                    "`amount` REAL NOT NULL, " +
                    "`categoryId` INTEGER NOT NULL, " +
                    "`date` INTEGER NOT NULL, " +
                    "`note` TEXT, " +
                    "`paymentMethod` TEXT, " +
                    "`createdAt` INTEGER NOT NULL)");
            db.execSQL("INSERT INTO `budgets_new` (id, type, amount, categoryId, date, note, paymentMethod, createdAt) " +
                    "SELECT id, type, amount, categoryId, date, note, paymentMethod, createdAt FROM `budgets`");
            db.execSQL("DROP TABLE `budgets`");
            db.execSQL("ALTER TABLE `budgets_new` RENAME TO `budgets`");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "smartwallet_database")
                            .addMigrations(MIGRATION_1_2)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // ✅ نستخدم db مباشرة بدلاً من INSTANCE لتجنّب Race Condition
            insertDefaultCategories(db);
        }
    };

    private static void insertDefaultCategories(SupportSQLiteDatabase db) {
        // Income categories
        insertCategory(db, "راتب", "ic_salary", "#4CAF50", 0);
        insertCategory(db, "مكافآت", "ic_bonus", "#2196F3", 0);
        insertCategory(db, "استثمارات", "ic_investment", "#9C27B0", 0);
        insertCategory(db, "هدية", "ic_gift", "#FF9800", 0);
        insertCategory(db, "أخرى", "ic_other", "#607D8B", 0);

        // Expense categories
        insertCategory(db, "طعام وشراب", "ic_food", "#FFCC80", 1);
        insertCategory(db, "مواصلات", "ic_transport", "#FFF59D", 1);
        insertCategory(db, "سكن وإيجار", "ic_housing", "#90CAF9", 1);
        insertCategory(db, "ترفيه", "ic_entertainment", "#F48FB1", 1);
        insertCategory(db, "صحة", "ic_health", "#A5D6A7", 1);
        insertCategory(db, "تسوق", "ic_shopping", "#CE93D8", 1);
        insertCategory(db, "تعليم", "ic_education", "#81D4FA", 1);
        insertCategory(db, "فواتير", "ic_bills", "#BCAAA4", 1);
        insertCategory(db, "أخرى", "ic_other", "#B0BEC5", 1);
    }

    private static void insertCategory(SupportSQLiteDatabase db, String name, String icon,
                                       String color, int type) {
        db.execSQL("INSERT INTO categories (name, icon, color, type) VALUES (?, ?, ?, ?)",
                new Object[]{name, icon, Color.parseColor(color), type});
    }
}