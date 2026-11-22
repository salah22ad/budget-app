package com.smartwallet.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.smartwallet.data.database.AppDatabase;
import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.Category;
import com.smartwallet.data.model.MonthlyAggregate;
import com.smartwallet.data.repository.BudgetRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetViewModel extends AndroidViewModel {

    private static final String PREFS_NAME = "smartwallet_prefs";
    private static final String KEY_MONTHLY_BUDGET = "monthly_budget";
    private static final double DEFAULT_MONTHLY_BUDGET = 5000.0;

    private final BudgetRepository repository;
    private final MutableLiveData<Calendar> currentMonth = new MutableLiveData<>();
    private final MutableLiveData<Double> monthlyBudget = new MutableLiveData<>(DEFAULT_MONTHLY_BUDGET);
    private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", new Locale("ar", "EG"));

    private final LiveData<List<Budget>> currentMonthBudgets;
    private final LiveData<Double> totalIncome;
    private final LiveData<Double> totalExpense;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        repository = new BudgetRepository(application);

        SharedPreferences prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        float saved = prefs.getFloat(KEY_MONTHLY_BUDGET, (float) DEFAULT_MONTHLY_BUDGET);
        monthlyBudget.setValue((double) saved);

        Calendar cal = Calendar.getInstance();
        currentMonth.setValue(cal);

        currentMonthBudgets = Transformations.switchMap(currentMonth, month -> {
            long[] range = getMonthRange(month);
            return repository.getBudgetsByDateRange(range[0], range[1]);
        });

        totalIncome = Transformations.switchMap(currentMonth, month -> {
            long[] range = getMonthRange(month);
            return repository.getTotalIncomeByDateRange(range[0], range[1]);
        });

        totalExpense = Transformations.switchMap(currentMonth, month -> {
            long[] range = getMonthRange(month);
            return repository.getTotalExpenseByDateRange(range[0], range[1]);
        });
    }

    public void goToPreviousMonth() {
        Calendar cal = currentMonth.getValue();
        if (cal != null) {
            Calendar newCal = (Calendar) cal.clone();
            newCal.add(Calendar.MONTH, -1);
            currentMonth.setValue(newCal);
        }
    }

    public void goToNextMonth() {
        Calendar cal = currentMonth.getValue();
        if (cal != null) {
            Calendar nextMonth = (Calendar) cal.clone();
            nextMonth.add(Calendar.MONTH, 1);

            // ✅ التحقق من عدم تجاوز الشهر الحالي
            Calendar currentRealMonth = Calendar.getInstance();
            // توحيد اليوم لتجنب أخطاء المقارنة
            nextMonth.set(Calendar.DAY_OF_MONTH, 1);
            currentRealMonth.set(Calendar.DAY_OF_MONTH, 1);

            if (!nextMonth.after(currentRealMonth)) {
                currentMonth.setValue(nextMonth);
            } else {
                Toast.makeText(getApplication(), "لا يمكن الانتقال إلى شهر مستقبلي", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Calendar getCurrentMonthValue() {
        Calendar cal = currentMonth.getValue();
        return cal != null ? cal : Calendar.getInstance();
    }

    public String getCurrentMonthFormatted() {
        Calendar cal = currentMonth.getValue();
        return cal != null ? monthYearFormat.format(cal.getTime()) : "";
    }

    public LiveData<Calendar> getCurrentMonth() { return currentMonth; }
    public LiveData<List<Budget>> getCurrentMonthBudgets() { return currentMonthBudgets; }
    public LiveData<Double> getTotalIncome() { return totalIncome; }
    public LiveData<Double> getTotalExpense() { return totalExpense; }
    public LiveData<Double> getMonthlyBudget() { return monthlyBudget; }

    public void setMonthlyBudget(double budget) {
        monthlyBudget.setValue(budget);
        getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putFloat(KEY_MONTHLY_BUDGET, (float) budget)
                .apply();
    }

    public void insert(Budget budget) { repository.insert(budget); }
    public void update(Budget budget) { repository.update(budget); }
    public void delete(Budget budget) { repository.delete(budget); }

    public LiveData<Budget> getBudgetById(long id) { return repository.getBudgetById(id); }
    public LiveData<List<Budget>> searchBudgets(String query) { return repository.searchBudgets(query); }

    public LiveData<List<MonthlyAggregate>> getLast6MonthsAggregates() {
        Calendar cal = currentMonth.getValue();
        if (cal == null) cal = Calendar.getInstance();

        Calendar start = (Calendar) cal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        start.add(Calendar.MONTH, -5);

        long[] endRange = getMonthRange(cal);
        return repository.getMonthlyAggregates(start.getTimeInMillis(), endRange[1]);
    }

    public LiveData<List<Category>> getExpenseCategories() {
        return AppDatabase.getDatabase(getApplication()).categoryDao().getExpenseCategories();
    }

    public LiveData<List<Category>> getIncomeCategories() {
        return AppDatabase.getDatabase(getApplication()).categoryDao().getIncomeCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return AppDatabase.getDatabase(getApplication()).categoryDao().getAllCategories();
    }

    private long[] getMonthRange(Calendar cal) {
        Calendar start = (Calendar) cal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) cal.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }

    public long[] getCurrentMonthRange() {
        Calendar cal = currentMonth.getValue();
        return cal != null ? getMonthRange(cal) : new long[]{0, 0};
    }
}
