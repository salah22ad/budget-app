package com.smartwallet.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.smartwallet.R;
import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.Category;
import com.smartwallet.databinding.ActivityAddBudgetBinding;
import com.smartwallet.viewmodel.BudgetViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddBudgetActivity extends AppCompatActivity {

    private ActivityAddBudgetBinding binding;
    private BudgetViewModel viewModel;

    private int budgetType = 1; // 1 = Expense, 0 = Income
    private long selectedDate = System.currentTimeMillis();
    private long selectedCategoryId = -1;
    private String selectedPaymentMethod;

    private long editingBudgetId = -1;
    private boolean editModeLoaded = false;
    private boolean observersSetup = false;

    private final MutableLiveData<Integer> currentType = new MutableLiveData<>(1);
    private MediatorLiveData<List<Category>> categoriesLive;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        // Defaults
        if (getIntent().hasExtra("type")) {
            budgetType = getIntent().getIntExtra("type", 1);
        }
        if (getIntent().hasExtra("budget_id")) {
            editingBudgetId = getIntent().getLongExtra("budget_id", -1);
        }

        updateTypeButtons();
        binding.etDate.setText(dateFormat.format(new Date(selectedDate)));
        setupPaymentChips();
        setupClickListeners();

        if (editingBudgetId != -1) {
            loadBudgetForEdit();
        } else {
            currentType.setValue(budgetType);
            setupObservers();
        }
    }

    private void updateTypeButtons() {
        if (budgetType == 0) {
            binding.btnIncome.setBackgroundTintList(getColorStateList(R.color.income_green));
            binding.btnExpense.setBackgroundTintList(getColorStateList(R.color.surface_elevated));
            binding.btnExpense.setTextColor(getColor(R.color.text_secondary));
            binding.btnIncome.setTextColor(getColor(R.color.text_on_primary));
            binding.tvTitle.setText(R.string.add_income);
        } else {
            binding.btnExpense.setBackgroundTintList(getColorStateList(R.color.expense_red));
            binding.btnIncome.setBackgroundTintList(getColorStateList(R.color.surface_elevated));
            binding.btnIncome.setTextColor(getColor(R.color.text_secondary));
            binding.btnExpense.setTextColor(getColor(R.color.text_on_primary));
            binding.tvTitle.setText(R.string.add_expense);
        }
    }

    private void setupPaymentChips() {
        selectedPaymentMethod = getString(R.string.cash);
        binding.chipGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipCash) {
                selectedPaymentMethod = getString(R.string.cash);
            } else if (checkedId == R.id.chipCredit) {
                selectedPaymentMethod = getString(R.string.credit_card);
            } else if (checkedId == R.id.chipTransfer) {
                selectedPaymentMethod = getString(R.string.bank_transfer);
            }
        });
    }

    private void checkPaymentChip(String method) {
        if (method == null) method = getString(R.string.cash);
        if (method.equals(getString(R.string.credit_card))) {
            binding.chipCredit.setChecked(true);
        } else if (method.equals(getString(R.string.bank_transfer))) {
            binding.chipTransfer.setChecked(true);
        } else {
            binding.chipCash.setChecked(true);
        }
    }

    // ✅ Single observer setup — no duplicates
    private void setupObservers() {
        if (observersSetup) return;
        observersSetup = true;

        LiveData<List<Category>> income = viewModel.getIncomeCategories();
        LiveData<List<Category>> expense = viewModel.getExpenseCategories();

        categoriesLive = new MediatorLiveData<>();
        categoriesLive.addSource(income, cats -> {
            Integer t = currentType.getValue();
            if (t != null && t == 0) categoriesLive.setValue(cats);
        });
        categoriesLive.addSource(expense, cats -> {
            Integer t = currentType.getValue();
            if (t != null && t == 1) categoriesLive.setValue(cats);
        });

        categoriesLive.observe(this, this::populateCategories);
    }

    private void populateCategories(List<Category> categories) {
        binding.chipGroupCategory.removeAllViews();
        if (categories == null) return;

        for (Category category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.getName());
            chip.setTag(category.getId());
            chip.setCheckable(true);
            chip.setChipCornerRadius(20f);

            // ✅ Preselect the saved category (edit mode)
            if (category.getId() == selectedCategoryId) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) selectedCategoryId = (long) buttonView.getTag();
            });

            binding.chipGroupCategory.addView(chip);
        }
    }

    private void setupClickListeners() {
        binding.btnClose.setOnClickListener(v -> finish());

        binding.btnExpense.setOnClickListener(v -> {
            budgetType = 1;
            currentType.setValue(1);
            updateTypeButtons();
            selectedCategoryId = -1;
        });

        binding.btnIncome.setOnClickListener(v -> {
            budgetType = 0;
            currentType.setValue(0);
            updateTypeButtons();
            selectedCategoryId = -1;
        });

        binding.etDate.setOnClickListener(v -> showDatePicker());
        binding.btnSave.setOnClickListener(v -> saveBudget());
    }

    private void loadBudgetForEdit() {
        viewModel.getBudgetById(editingBudgetId).observe(this, budget -> {
            if (editModeLoaded || budget == null) return;
            editModeLoaded = true;

            budgetType = budget.getType();
            currentType.setValue(budgetType);
            selectedDate = budget.getDate();
            selectedCategoryId = budget.getCategoryId();

            binding.etAmount.setText(String.valueOf(budget.getAmount()));
            if (budget.getNote() != null) binding.etNote.setText(budget.getNote());
            binding.etDate.setText(dateFormat.format(new Date(selectedDate)));
            binding.tvTitle.setText("تعديل العملية");

            updateTypeButtons();
            checkPaymentChip(budget.getPaymentMethod());

            // Now attach observers → categories will check the saved chip
            setupObservers();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDate);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            selectedDate = selected.getTimeInMillis();
            binding.etDate.setText(dateFormat.format(new Date(selectedDate)));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveBudget() {
        String amountStr = binding.etAmount.getText() != null
                ? binding.etAmount.getText().toString().trim() : "";
        if (amountStr.isEmpty()) {
            binding.tilAmount.setError(getString(R.string.enter_amount));
            return;
        }
        binding.tilAmount.setError(null);

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.tilAmount.setError(getString(R.string.enter_amount));
            return;
        }
        if (amount <= 0) {
            binding.tilAmount.setError(getString(R.string.enter_amount));
            return;
        }

        if (selectedCategoryId == -1) {
            Toast.makeText(this, R.string.select_category, Toast.LENGTH_SHORT).show();
            return;
        }

        String note = binding.etNote.getText() != null
                ? binding.etNote.getText().toString().trim() : "";

        Budget budget = new Budget();
        budget.setType(budgetType);
        budget.setAmount(amount);
        budget.setCategoryId(selectedCategoryId);
        budget.setDate(selectedDate);
        budget.setNote(note);
        budget.setPaymentMethod(selectedPaymentMethod);

        if (editingBudgetId != -1) {
            budget.setId(editingBudgetId);
            viewModel.update(budget);
            Toast.makeText(this, "تم التحديث", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(budget);
            Toast.makeText(this, R.string.save, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}