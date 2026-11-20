package com.smartwallet.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smartwallet.R;
import com.smartwallet.databinding.FragmentSettingsBinding;
import com.smartwallet.viewmodel.BudgetViewModel;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "smartwallet_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private FragmentSettingsBinding binding;
    private BudgetViewModel viewModel;
    private boolean isDarkMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        setupUI();
        setupClickListeners();
    }

    private void setupUI() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // ✅ قراءة الوضع الليلي من التفضيلات (وليس من AppCompatDelegate مباشرة)
        isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        binding.switchDarkMode.setChecked(isDarkMode);

        // ✅ قراءة الميزانية من ViewModel (المحمّلة من SharedPreferences)
        Double budget = viewModel.getMonthlyBudget().getValue();
        if (budget != null) {
            binding.etBudget.setText(String.valueOf(budget.intValue()));
        }

        viewModel.getMonthlyBudget().observe(getViewLifecycleOwner(), b -> {
            if (b != null) binding.etBudget.setText(String.valueOf(b.intValue()));
        });
    }

    private void setupClickListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDarkMode = isChecked;
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_DARK_MODE, isChecked)
                    .apply();

            AppCompatDelegate.setDefaultNightMode(isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // ✅ حفظ الميزانية
        binding.btnSaveBudget.setOnClickListener(v -> saveBudget());

        // ✅ Placeholder للتصدير — نُظهر Toast توضيحي بدل الصمت
        binding.cardExportPdf.setOnClickListener(v ->
                Toast.makeText(requireContext(), "قريباً: تصدير PDF", Toast.LENGTH_SHORT).show());
        binding.cardExportCsv.setOnClickListener(v ->
                Toast.makeText(requireContext(), "قريباً: تصدير CSV", Toast.LENGTH_SHORT).show());
    }

    private void saveBudget() {
        String raw = binding.etBudget.getText() != null
                ? binding.etBudget.getText().toString().trim() : "";
        if (raw.isEmpty()) {
            binding.tilBudget.setError(getString(R.string.enter_valid_amount));
            return;
        }

        double value;
        try {
            value = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            binding.tilBudget.setError(getString(R.string.enter_valid_amount));
            return;
        }

        if (value <= 0) {
            binding.tilBudget.setError(getString(R.string.enter_valid_amount));
            return;
        }

        binding.tilBudget.setError(null);
        viewModel.setMonthlyBudget(value);
        Toast.makeText(requireContext(), R.string.budget_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}