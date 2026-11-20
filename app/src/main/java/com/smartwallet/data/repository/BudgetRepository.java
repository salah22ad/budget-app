package com.smartwallet.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.smartwallet.data.database.AppDatabase;
import com.smartwallet.data.database.BudgetDao;
import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.BudgetWithCategory;
import com.smartwallet.data.model.MonthlyAggregate;

import java.util.ArrayList;
import java.util.List;

public class BudgetRepository {

    private final BudgetDao budgetDao;

    public BudgetRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        budgetDao = db.budgetDao();
    }

    public LiveData<List<Budget>> getAllBudgets() {
        return Transformations.map(budgetDao.getAllBudgets(), this::mapToBudgets);
    }

    public LiveData<Budget> getBudgetById(long id) {
        return Transformations.map(budgetDao.getBudgetWithCategoryById(id), bwc -> {
            if (bwc == null) return null;
            return toBudget(bwc);
        });
    }

    public LiveData<List<Budget>> getBudgetsByDateRange(long startDate, long endDate) {
        return Transformations.map(
                budgetDao.getBudgetsByDateRange(startDate, endDate),
                this::mapToBudgets);
    }

    public LiveData<List<Budget>> getBudgetsByType(int type) {
        return Transformations.map(budgetDao.getBudgetsByType(type), this::mapToBudgets);
    }

    public LiveData<List<Budget>> getBudgetsByTypeAndDateRange(int type, long startDate, long endDate) {
        return Transformations.map(
                budgetDao.getBudgetsByTypeAndDateRange(type, startDate, endDate),
                this::mapToBudgets);
    }

    public LiveData<Double> getTotalIncomeByDateRange(long startDate, long endDate) {
        return budgetDao.getTotalIncomeByDateRange(startDate, endDate);
    }

    public LiveData<Double> getTotalExpenseByDateRange(long startDate, long endDate) {
        return budgetDao.getTotalExpenseByDateRange(startDate, endDate);
    }

    public LiveData<List<Budget>> searchBudgets(String query) {
        return Transformations.map(budgetDao.searchBudgets(query), this::mapToBudgets);
    }

    /** ✅ بيانات المخطط الشريطي الشهري */
    public LiveData<List<MonthlyAggregate>> getMonthlyAggregates(long startDate, long endDate) {
        return budgetDao.getMonthlyAggregates(startDate, endDate);
    }

    public void insert(Budget budget) {
        AppDatabase.databaseWriteExecutor.execute(() -> budgetDao.insert(budget));
    }

    public void update(Budget budget) {
        AppDatabase.databaseWriteExecutor.execute(() -> budgetDao.update(budget));
    }

    public void delete(Budget budget) {
        AppDatabase.databaseWriteExecutor.execute(() -> budgetDao.delete(budget));
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> budgetDao.deleteAll());
    }

    private List<Budget> mapToBudgets(List<BudgetWithCategory> source) {
        List<Budget> result = new ArrayList<>();
        if (source == null) return result;
        for (BudgetWithCategory bwc : source) result.add(toBudget(bwc));
        return result;
    }

    private Budget toBudget(BudgetWithCategory bwc) {
        Budget b = bwc.budget;
        if (bwc.category != null) {
            b.setCategoryName(bwc.category.getName());
            b.setCategoryColor(bwc.category.getColor());
            b.setCategoryIcon(bwc.category.getIcon());
        }
        return b;
    }
}