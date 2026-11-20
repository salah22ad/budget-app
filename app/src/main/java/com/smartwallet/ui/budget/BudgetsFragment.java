package com.smartwallet.ui.budget;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.smartwallet.R;
import com.smartwallet.adapter.BudgetAdapter;
import com.smartwallet.data.model.Budget;
import com.smartwallet.databinding.FragmentBudgetsBinding;
import com.smartwallet.viewmodel.BudgetViewModel;

import java.util.ArrayList;
import java.util.List;

public class BudgetsFragment extends Fragment implements BudgetAdapter.OnBudgetClickListener {

    private FragmentBudgetsBinding binding;
    private BudgetViewModel viewModel;
    private BudgetAdapter adapter;

    private int currentFilter = 0; // 0 = all, 1 = income, 2 = expense
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private LiveData<List<Budget>> displayBudgets;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBudgetsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        setupUI();
        setupObservers();
        setupClickListeners();
    }

    private void setupUI() {
        adapter = new BudgetAdapter(requireContext(), this);
        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBudgets.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
    }

    private void setupObservers() {
        // Month label
        viewModel.getCurrentMonth().observe(getViewLifecycleOwner(), cal -> updateMonthDisplay());

        // ✅ Single source of truth for list (respects search + month)
        displayBudgets = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return viewModel.getCurrentMonthBudgets();
            }
            return viewModel.searchBudgets(query);
        });

        displayBudgets.observe(getViewLifecycleOwner(), budgets -> {
            updateBudgetsList(budgets);
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void updateMonthDisplay() {
        binding.tvCurrentMonth.setText(viewModel.getCurrentMonthFormatted());
    }

    private void updateBudgetsList(List<Budget> budgets) {
        if (budgets == null) budgets = new ArrayList<>();

        List<Budget> filtered = new ArrayList<>();
        for (Budget b : budgets) {
            if (currentFilter == 0) filtered.add(b);
            else if (currentFilter == 1 && b.isIncome()) filtered.add(b);
            else if (currentFilter == 2 && b.isExpense()) filtered.add(b);
        }

        if (filtered.isEmpty()) {
            binding.rvBudgets.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.VISIBLE);
        } else {
            binding.rvBudgets.setVisibility(View.VISIBLE);
            binding.emptyState.setVisibility(View.GONE);
            adapter.setBudgets(filtered);
        }
    }

    private void setupClickListeners() {
        binding.btnPreviousMonth.setOnClickListener(v -> viewModel.goToPreviousMonth());
        binding.btnNextMonth.setOnClickListener(v -> viewModel.goToNextMonth());

        binding.chipAll.setOnCheckedChangeListener((b, checked) -> {
            if (checked) { currentFilter = 0; refreshFromSource(); }
        });
        binding.chipIncome.setOnCheckedChangeListener((b, checked) -> {
            if (checked) { currentFilter = 1; refreshFromSource(); }
        });
        binding.chipExpense.setOnCheckedChangeListener((b, checked) -> {
            if (checked) { currentFilter = 2; refreshFromSource(); }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                searchQuery.setValue(s.toString().trim());
            }
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void refreshFromSource() {
        if (displayBudgets != null && displayBudgets.getValue() != null) {
            updateBudgetsList(displayBudgets.getValue());
        }
    }

    // --- Adapter callbacks ---

    @Override
    public void onBudgetClick(Budget budget) {
        // future: open detail
    }

//    @Override
//    public void onEditClick(Budget budget) {
//        Intent intent = new Intent(requireContext(), AddBudgetActivity.class);
//        intent.putExtra("budget_id", budget.getId());
//        intent.putExtra("type", budget.getType());
//        startActivity(intent);
//    }
@Override
public void onEditClick(Budget budget) {
    AddBudgetDialog dialog = new AddBudgetDialog();
    Bundle b = new Bundle();
    b.putLong("budget_id", budget.getId());
    b.putInt("type", budget.getType());
    dialog.setArguments(b);
    dialog.show(getChildFragmentManager(), "EditBudget");
}
    @Override
    public void onDeleteClick(Budget budget) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete)
                .setMessage("هل تريد حذف هذه العملية؟")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    viewModel.delete(budget);
                    Snackbar.make(binding.getRoot(), "تم الحذف", Snackbar.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMonthDisplay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}