package com.smartwallet.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.BudgetWithCategory;
import com.smartwallet.data.model.MonthlyAggregate;

import java.util.List;

@Dao
public interface BudgetDao {

    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Transaction
    @Query("SELECT * FROM budgets ORDER BY date DESC")
    LiveData<List<BudgetWithCategory>> getAllBudgets();

    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :id")
    LiveData<BudgetWithCategory> getBudgetWithCategoryById(long id);

    @Query("SELECT * FROM budgets WHERE id = :id")
    Budget getBudgetById(long id);

    @Transaction
    @Query("SELECT * FROM budgets WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    LiveData<List<BudgetWithCategory>> getBudgetsByDateRange(long startDate, long endDate);

    @Transaction
    @Query("SELECT * FROM budgets WHERE type = :type ORDER BY date DESC")
    LiveData<List<BudgetWithCategory>> getBudgetsByType(int type);

    @Transaction
    @Query("SELECT * FROM budgets WHERE type = :type AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    LiveData<List<BudgetWithCategory>> getBudgetsByTypeAndDateRange(int type, long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM budgets WHERE type = 0 AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getTotalIncomeByDateRange(long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM budgets WHERE type = 1 AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getTotalExpenseByDateRange(long startDate, long endDate);

    @Transaction
    @Query("SELECT budgets.* FROM budgets LEFT JOIN categories ON budgets.categoryId = categories.id " +
            "WHERE budgets.note LIKE '%' || :query || '%' OR categories.name LIKE '%' || :query || '%' " +
            "ORDER BY budgets.date DESC")
    LiveData<List<BudgetWithCategory>> searchBudgets(String query);

    /**
     * ✅ تجميع شهري لكل نوع (دخل/مصروف) خلال فترة معطاة
     * يُستخدم في ReportsFragment لبناء المخطط الشريطي ببيانات حقيقية
     * date/1000 يحوّل من ميلي ثانية إلى ثوانٍ ثم 'unixepoch' يستخرج الشهر
     */
    @Query("SELECT type AS type, " +
            "strftime('%Y-%m', date/1000, 'unixepoch') AS month, " +
            "SUM(amount) AS total " +
            "FROM budgets WHERE date >= :startDate AND date <= :endDate " +
            "GROUP BY type, month ORDER BY month ASC")
    LiveData<List<MonthlyAggregate>> getMonthlyAggregates(long startDate, long endDate);

    @Query("DELETE FROM budgets")
    void deleteAll();
}