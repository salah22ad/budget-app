package com.smartwallet.ui.budget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.smartwallet.R;
import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.Category;
import com.smartwallet.databinding.DialogAddBudgetBinding;
import com.smartwallet.util.IconUtils;
import com.smartwallet.viewmodel.BudgetViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddBudgetDialog extends DialogFragment {

    private DialogAddBudgetBinding binding;
    private BudgetViewModel viewModel;

    private int budgetType = 1; // 1 = Expense, 0 = Income
    private long selectedDate = System.currentTimeMillis();
    private long selectedCategoryId = -1;
    private String selectedPaymentMethod;

    private long editingBudgetId = -1;
    private boolean editModeLoaded = false;
    private boolean observersSetup = false;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        if (getArguments() != null) {
            if (getArguments().containsKey("type")) {
                budgetType = getArguments().getInt("type", 1);
            }
            if (getArguments().containsKey("budget_id")) {
                editingBudgetId = getArguments().getLong("budget_id", -1);
            }
        }

        applyThemeColors();
        binding.etDate.setText(dateFormat.format(new Date(selectedDate)));
        setupPaymentDropdown();
        setupClickListeners();

        if (editingBudgetId != -1) {
            loadBudgetForEdit();
        } else {
            setupObservers();
        }
    }

    // ✅ ضبط حجم الحوار هنا لضمان أخذ المساحة الصحيحة
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // نعطيه عرض الشاشة (مع مراعاة الحواف من ملف XML) وارتفاع تلقائي
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // ✅ تغيير ألوان العنوان والزر حسب النوع (أحمر للمصروف، أخضر للدخل)
    private void applyThemeColors() {
        int color;
        if (budgetType == 0) {
            binding.tvTitle.setText(R.string.add_income);
            color = ContextCompat.getColor(requireContext(), R.color.income_green);
        } else {
            binding.tvTitle.setText(R.string.add_expense);
            color = ContextCompat.getColor(requireContext(), R.color.expense_red);
        }
        binding.tvTitle.setTextColor(color);
        binding.btnSave.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setupPaymentDropdown() {
        String[] paymentMethods = {
                getString(R.string.cash),
                getString(R.string.credit_card),
                getString(R.string.bank_transfer),
                getString(R.string.digital_wallet)
        };
        selectedPaymentMethod = paymentMethods[0];

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentMethods);
        binding.autoCompletePayment.setAdapter(adapter);
        binding.autoCompletePayment.setText(selectedPaymentMethod, false);

        binding.autoCompletePayment.setOnItemClickListener((parent, view, position, id) -> {
            selectedPaymentMethod = (String) parent.getItemAtPosition(position);
        });
    }

    private void setupObservers() {
        if (observersSetup) return;
        observersSetup = true;

        if (budgetType == 0) {
            viewModel.getIncomeCategories().observe(getViewLifecycleOwner(), this::populateCategories);
        } else {
            viewModel.getExpenseCategories().observe(getViewLifecycleOwner(), this::populateCategories);
        }
    }

    private void populateCategories(List<Category> categories) {
        binding.chipGroupCategory.removeAllViews();
        if (categories == null) return;

        for (Category category : categories) {
            Chip chip = new Chip(requireContext());
            ChipDrawable drawable = ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.CustomChoiceChip);
            chip.setChipDrawable(drawable);

            chip.setText(category.getName());
            chip.setTag(category.getId());
            chip.setCheckable(true);

            // إظهار الأيقونة بلون الفئة
            int iconRes = IconUtils.getIconResource(requireContext(), category.getIcon());
            if (iconRes != 0) {
                chip.setChipIcon(requireContext().getDrawable(iconRes));
                chip.setChipIconVisible(true);
                chip.setChipIconSize(20f);
                if (chip.getChipIcon() != null) {
                    chip.getChipIcon().setTint(category.getColor());
                }
            }

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
        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.etDate.setOnClickListener(v -> showDatePicker());
        binding.btnSave.setOnClickListener(v -> saveBudget());
    }

    private void loadBudgetForEdit() {
        viewModel.getBudgetById(editingBudgetId).observe(getViewLifecycleOwner(), budget -> {
            if (editModeLoaded || budget == null) return;
            editModeLoaded = true;

            budgetType = budget.getType();
            selectedDate = budget.getDate();
            selectedCategoryId = budget.getCategoryId();

            binding.etAmount.setText(String.valueOf(budget.getAmount()));
            if (budget.getNote() != null) binding.etNote.setText(budget.getNote());
            binding.etDate.setText(dateFormat.format(new Date(selectedDate)));
            binding.tvTitle.setText("تعديل العملية");

            if (budget.getPaymentMethod() != null) {
                binding.autoCompletePayment.setText(budget.getPaymentMethod(), false);
                selectedPaymentMethod = budget.getPaymentMethod();
            }

            applyThemeColors(); // لتحديث الألوان بعد تحميل النوع
            setupObservers();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDate);
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, day);
            selectedDate = selected.getTimeInMillis();
            binding.etDate.setText(dateFormat.format(new Date(selectedDate)));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveBudget() {
        String amountStr = binding.etAmount.getText() != null ? binding.etAmount.getText().toString().trim() : "";
        if (amountStr.isEmpty()) {
            binding.tilAmount.setError(getString(R.string.enter_amount));
            return;
        }

        double amount;
        try { amount = Double.parseDouble(amountStr); }
        catch (NumberFormatException e) { binding.tilAmount.setError(getString(R.string.enter_amount)); return; }

        if (amount <= 0) { binding.tilAmount.setError(getString(R.string.enter_amount)); return; }

        if (selectedCategoryId == -1) {
            Toast.makeText(requireContext(), R.string.select_category, Toast.LENGTH_SHORT).show();
            return;
        }

        String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : "";

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
            Toast.makeText(requireContext(), "تم التحديث", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(budget);
            Toast.makeText(requireContext(), R.string.save, Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}