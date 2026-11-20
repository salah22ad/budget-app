package com.smartwallet.ui.report;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.smartwallet.R;
import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.MonthlyAggregate;
import com.smartwallet.databinding.FragmentReportsBinding;
import com.smartwallet.util.FormatUtils;
import com.smartwallet.viewmodel.BudgetViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private BudgetViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
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
        setupBarChart();
        setupPieChart();
    }

    private void setupBarChart() {
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.setDrawBarShadow(false);
        binding.barChart.setHighlightFullBarEnabled(false);
        binding.barChart.setDrawValueAboveBar(true);
        binding.barChart.setPinchZoom(false);
        binding.barChart.setDoubleTapToZoomEnabled(false);
        binding.barChart.setScaleEnabled(false);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

        binding.barChart.getAxisLeft()
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.getLegend().setEnabled(true);
        binding.barChart.getLegend()
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));

        binding.barChart.animateY(1000);
    }

    private void setupPieChart() {
        if (binding.pieChart == null) return;
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.TRANSPARENT);
        binding.pieChart.setHoleRadius(50f);
        binding.pieChart.setTransparentCircleRadius(55f);
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.setEntryLabelColor(Color.WHITE);
        binding.pieChart.setEntryLabelTextSize(10f);
    }

    private void setupObservers() {
        viewModel.getCurrentMonth().observe(getViewLifecycleOwner(), cal -> updateMonthDisplay());

        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            double v = income != null ? income : 0.0;
            binding.tvTotalIncome.setText(FormatUtils.formatCurrency(v));
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            double v = expense != null ? expense : 0.0;
            binding.tvTotalExpense.setText(FormatUtils.formatCurrency(v));
            updateProgress(v);
        });

        viewModel.getMonthlyBudget().observe(getViewLifecycleOwner(), budget -> {
            double b = budget != null ? budget : 0.0;
            binding.tvBudget.setText(FormatUtils.formatCurrency(b));
            Double exp = viewModel.getTotalExpense().getValue();
            updateProgress(exp != null ? exp : 0.0);
        });

        viewModel.getLast6MonthsAggregates().observe(getViewLifecycleOwner(), this::updateBarChart);
        viewModel.getCurrentMonthBudgets().observe(getViewLifecycleOwner(), this::updatePieChart);
    }

    private void updateMonthDisplay() {
        binding.tvCurrentMonth.setText(viewModel.getCurrentMonthFormatted());
    }

    private void updateProgress(double expense) {
        Double budgetObj = viewModel.getMonthlyBudget().getValue();
        double budget = budgetObj != null ? budgetObj : 0;
        int pct = budget > 0 ? (int) Math.min((expense / budget) * 100, 100) : 0;
        binding.progressBudget.setProgress(pct);
        binding.tvSpentPercentage.setText(pct + "%");
    }

    private void updateBarChart(List<MonthlyAggregate> aggregates) {
        if (aggregates == null || aggregates.isEmpty()) {
            binding.barChart.clear();
            binding.barChart.invalidate();
            return;
        }

        Calendar cal = viewModel.getCurrentMonthValue();
        Calendar start = (Calendar) cal.clone();
        start.add(Calendar.MONTH, -5);

        Map<String, Float> incomeMap = new HashMap<>();
        Map<String, Float> expenseMap = new HashMap<>();

        for (MonthlyAggregate agg : aggregates) {
            if (agg.month == null) continue;
            if (agg.type == 0) incomeMap.put(agg.month, (float) agg.total);
            else expenseMap.put(agg.month, (float) agg.total);
        }

        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        String[] labels = new String[6];

        // ✅ استخدام Locale.US لمطابقة مخرجات SQLite
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
        SimpleDateFormat labelFormat = new SimpleDateFormat("MMM", FormatUtils.AR_EG);

        Calendar cursor = (Calendar) start.clone();
        for (int i = 0; i < 6; i++) {
            String key = keyFormat.format(cursor.getTime());
            labels[i] = labelFormat.format(cursor.getTime());

            incomeEntries.add(new BarEntry(i, incomeMap.getOrDefault(key, 0f)));
            expenseEntries.add(new BarEntry(i, expenseMap.getOrDefault(key, 0f)));
            cursor.add(Calendar.MONTH, 1);
        }

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "الدخل");
        incomeSet.setColor(ContextCompat.getColor(requireContext(), R.color.income_green));
        incomeSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        incomeSet.setValueTextSize(9f);

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "المصروفات");
        expenseSet.setColor(ContextCompat.getColor(requireContext(), R.color.expense_red));
        expenseSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        expenseSet.setValueTextSize(9f);

        BarData data = new BarData(incomeSet, expenseSet);
        data.setBarWidth(0.35f);

        binding.barChart.setData(data);
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.getXAxis().setLabelCount(6);

        float groupSpace = 0.15f;
        float barSpace = 0.05f;
        float barWidth = 0.35f;

        binding.barChart.getXAxis().setAxisMinimum(0f);
        binding.barChart.getXAxis().setAxisMaximum(0f + binding.barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 6);

        // ✅ تجميع الأعمدة بشكل صحيح
        binding.barChart.groupBars(0f, groupSpace, barSpace);
        binding.barChart.invalidate();
    }

    private void updatePieChart(List<Budget> budgets) {
        if (binding.pieChart == null) return;
        if (budgets == null || budgets.isEmpty()) {
            binding.pieChart.setData(null);
            binding.pieChart.invalidate();
            return;
        }

        Map<Long, Double> totals = new HashMap<>();
        double total = 0;

        for (Budget b : budgets) {
            if (b.isExpense()) {
                total += b.getAmount();
                totals.put(b.getCategoryId(), totals.getOrDefault(b.getCategoryId(), 0.0) + b.getAmount());
            }
        }

        if (totals.isEmpty() || total == 0) {
            binding.pieChart.setData(null);
            binding.pieChart.invalidate();
            return;
        }

        int[] chartColors = {
                ContextCompat.getColor(requireContext(), R.color.chart_color_1),
                ContextCompat.getColor(requireContext(), R.color.chart_color_2),
                ContextCompat.getColor(requireContext(), R.color.chart_color_3),
                ContextCompat.getColor(requireContext(), R.color.chart_color_4),
                ContextCompat.getColor(requireContext(), R.color.chart_color_5),
                ContextCompat.getColor(requireContext(), R.color.chart_color_6),
                ContextCompat.getColor(requireContext(), R.color.chart_color_7),
                ContextCompat.getColor(requireContext(), R.color.chart_color_8)
        };

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        int i = 0;
        for (Double v : totals.values()) {
            entries.add(new PieEntry((float) ((v / total) * 100), ""));
            colors.add(chartColors[i % chartColors.length]);
            i++;
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(colors);
        ds.setSliceSpace(2f);
        ds.setDrawValues(false);

        binding.pieChart.setData(new PieData(ds));
        binding.pieChart.invalidate();
    }

    private void setupClickListeners() {
        binding.btnPreviousMonth.setOnClickListener(v -> viewModel.goToPreviousMonth());
        binding.btnNextMonth.setOnClickListener(v -> viewModel.goToNextMonth());
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