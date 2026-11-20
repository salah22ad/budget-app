package com.smartwallet.ui.main;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry; // ✅ تم إصلاح الخطأ هنا
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.smartwallet.R;
import com.smartwallet.adapter.CategorySummaryAdapter;
import com.smartwallet.data.model.Budget;
import com.smartwallet.data.model.MonthlyAggregate;
import com.smartwallet.databinding.FragmentHomeBinding;

import com.smartwallet.ui.budget.AddBudgetDialog;
import com.smartwallet.util.FormatUtils;
import com.smartwallet.util.PieMarker;
import com.smartwallet.viewmodel.BudgetViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BudgetViewModel viewModel;
    private CategorySummaryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
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
        setupPieChart();
        setupBarChart();

        categoryAdapter = new CategorySummaryAdapter(requireContext());
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategories.setAdapter(categoryAdapter);

        binding.progressBudget.setProgress(0);
        binding.tvConsumedPercentage.setText("0%");
        binding.tvRemaining.setText("المتبقي: 0 " + getString(R.string.egp));
    }

    private void setupPieChart() {
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.TRANSPARENT);
        binding.pieChart.setHoleRadius(55f);
        binding.pieChart.setTransparentCircleRadius(60f);
        binding.pieChart.setRotationAngle(-90);
        binding.pieChart.setHighlightPerTapEnabled(true);
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.setDrawEntryLabels(false); // إخفاء النصوص الافتراضية

        // ✅ ربط التلميح الأنيق
        binding.pieChart.setMarker(new PieMarker(requireContext()));
    }

    private void setupBarChart() {
        binding.barChartHome.getDescription().setEnabled(false);
        binding.barChartHome.setDrawGridBackground(false);
        binding.barChartHome.setDrawBarShadow(false);
        binding.barChartHome.setHighlightFullBarEnabled(false);
        binding.barChartHome.setDrawValueAboveBar(true);
        binding.barChartHome.setPinchZoom(false);
        binding.barChartHome.setDoubleTapToZoomEnabled(false);
        binding.barChartHome.setScaleEnabled(false);

        XAxis xAxis = binding.barChartHome.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

        binding.barChartHome.getAxisLeft().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        binding.barChartHome.getAxisRight().setEnabled(false);
        binding.barChartHome.getLegend().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
    }

    private void setupObservers() {
        viewModel.getCurrentMonth().observe(getViewLifecycleOwner(), cal -> binding.tvCurrentMonth.setText(viewModel.getCurrentMonthFormatted()));

        viewModel.getCurrentMonthBudgets().observe(getViewLifecycleOwner(), this::updateBudgetsData);

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            double v = expense != null ? expense : 0.0;
            binding.tvTotalExpense.setText(FormatUtils.formatCurrency(v));
            binding.tvHomeTotalExpense.setText(FormatUtils.formatCurrency(v));
            updateBudgetProgress(v);
        });

        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            double v = income != null ? income : 0.0;
            binding.tvHomeTotalIncome.setText(FormatUtils.formatCurrency(v));
        });

        viewModel.getMonthlyBudget().observe(getViewLifecycleOwner(), budget -> {
            Double exp = viewModel.getTotalExpense().getValue();
            updateBudgetProgress(exp != null ? exp : 0.0);
        });

        viewModel.getLast6MonthsAggregates().observe(getViewLifecycleOwner(), this::updateBarChart);
    }

    private void updateBudgetsData(List<Budget> budgets) {
        if (budgets == null || budgets.isEmpty()) {
            binding.pieChart.setData(null);
            binding.pieChart.invalidate();
            categoryAdapter.setCategories(new ArrayList<>());
            return;
        }

        Map<Long, Double> categoryTotals = new HashMap<>();
        Map<Long, String> categoryNames = new HashMap<>();
        double totalExpense = 0;

        for (Budget budget : budgets) {
            if (budget.isExpense()) {
                totalExpense += budget.getAmount();
                long catId = budget.getCategoryId();
                categoryTotals.put(catId, categoryTotals.getOrDefault(catId, 0.0) + budget.getAmount());
                if (budget.getCategoryName() != null) categoryNames.put(catId, budget.getCategoryName());
            }
        }

        List<CategorySummaryAdapter.CategorySummary> summaries = new ArrayList<>();
        int[] chartColors = getChartColors();
        int index = 0;

        for (Map.Entry<Long, Double> entry : categoryTotals.entrySet()) {
            String name = categoryNames.getOrDefault(entry.getKey(), getString(R.string.category_other));
            int color = chartColors[index % chartColors.length];
            double amount = entry.getValue();
            double percentage = totalExpense > 0 ? (amount / totalExpense) * 100 : 0;
            summaries.add(new CategorySummaryAdapter.CategorySummary(name, color, amount, (float) percentage));
            index++;
        }

        updatePieChart(summaries);
        categoryAdapter.setCategories(summaries);
    }

    private void updatePieChart(List<CategorySummaryAdapter.CategorySummary> summaries) {
        if (summaries.isEmpty()) {
            binding.pieChart.setData(null);
            binding.pieChart.invalidate();
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (CategorySummaryAdapter.CategorySummary summary : summaries) {
            if (summary.percentage > 0) {
                // ✅ نمرر الاسم والمبلغ بداخل الـ Entry لاستخدامه في التلميح (Marker)
                String data = summary.name + "|" + FormatUtils.formatCurrency(summary.amount);
                PieEntry pieEntry = new PieEntry(summary.percentage, "");
                pieEntry.setData(data);
                entries.add(pieEntry);
                colors.add(summary.color);
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        dataSet.setDrawValues(false);

        binding.pieChart.setData(new PieData(dataSet));
        binding.pieChart.invalidate();
    }

    private void updateBarChart(List<MonthlyAggregate> aggregates) {
        if (aggregates == null || aggregates.isEmpty()) {
            binding.barChartHome.clear();
            binding.barChartHome.invalidate();
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

        binding.barChartHome.setData(data);
        binding.barChartHome.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChartHome.getXAxis().setLabelCount(6);

        float groupSpace = 0.15f;
        float barSpace = 0.05f;
        binding.barChartHome.getXAxis().setAxisMinimum(0f);
        binding.barChartHome.getXAxis().setAxisMaximum(0f + binding.barChartHome.getBarData().getGroupWidth(groupSpace, barSpace) * 6);
        binding.barChartHome.groupBars(0f, groupSpace, barSpace);
        binding.barChartHome.invalidate();
    }

    private int[] getChartColors() {
        return new int[]{
                ContextCompat.getColor(requireContext(), R.color.chart_color_1),
                ContextCompat.getColor(requireContext(), R.color.chart_color_2),
                ContextCompat.getColor(requireContext(), R.color.chart_color_3),
                ContextCompat.getColor(requireContext(), R.color.chart_color_4),
                ContextCompat.getColor(requireContext(), R.color.chart_color_5),
                ContextCompat.getColor(requireContext(), R.color.chart_color_6),
                ContextCompat.getColor(requireContext(), R.color.chart_color_7),
                ContextCompat.getColor(requireContext(), R.color.chart_color_8)
        };
    }

    private void updateBudgetProgress(double expense) {
        Double budget = viewModel.getMonthlyBudget().getValue();
        if (budget == null) budget = 5000.0;

        double consumed = budget > 0 ? (expense / budget) * 100 : 0;
        int consumedInt = (int) Math.min(consumed, 100);

        binding.progressBudget.setProgress(consumedInt);
        binding.tvConsumedPercentage.setText((int) consumed + "%");

        double remaining = budget - expense;
        binding.tvRemaining.setText("المتبقي: " + FormatUtils.formatCurrency(Math.abs(remaining)));
    }

    private void setupClickListeners() {
        binding.btnPreviousMonth.setOnClickListener(v -> viewModel.goToPreviousMonth());
        binding.btnNextMonth.setOnClickListener(v -> viewModel.goToNextMonth());

        binding.btnAddIncome.setOnClickListener(v -> {
            AddBudgetDialog dialog = new AddBudgetDialog();
            Bundle b = new Bundle(); b.putInt("type", 0); dialog.setArguments(b);
            dialog.show(getChildFragmentManager(), "AddBudget");
        });

        binding.btnAddExpense.setOnClickListener(v -> {
            AddBudgetDialog dialog = new AddBudgetDialog();
            Bundle b = new Bundle(); b.putInt("type", 1); dialog.setArguments(b);
            dialog.show(getChildFragmentManager(), "AddBudget");
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}