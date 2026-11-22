package com.smartwallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwallet.R;
import com.smartwallet.data.model.Budget;
import com.smartwallet.databinding.ItemBudgetBinding;
import com.smartwallet.util.FormatUtils;
import com.smartwallet.util.IconUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final Context context;
    private final List<Budget> budgets = new ArrayList<>();
    private final OnBudgetClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.US);

    public interface OnBudgetClickListener {
        void onBudgetClick(Budget budget);
        void onEditClick(Budget budget);
        void onDeleteClick(Budget budget);
    }

    public BudgetAdapter(Context context, OnBudgetClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setBudgets(List<Budget> newBudgets) {
        budgets.clear();
        if (newBudgets != null) budgets.addAll(newBudgets);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBudgetBinding binding = ItemBudgetBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BudgetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        holder.bind(budgets.get(position));
    }

    @Override
    public int getItemCount() { return budgets.size(); }

    class BudgetViewHolder extends RecyclerView.ViewHolder {

        private final ItemBudgetBinding binding;

        BudgetViewHolder(ItemBudgetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Budget budget) {
            binding.tvCategoryName.setText(
                    budget.getCategoryName() != null
                            ? budget.getCategoryName()
                            : context.getString(R.string.category_other));

            if (budget.getNote() != null && !budget.getNote().isEmpty()) {
                binding.tvNote.setVisibility(View.VISIBLE);
                binding.tvNote.setText(budget.getNote());
            } else {
                binding.tvNote.setVisibility(View.GONE);
            }

            binding.tvDate.setText(dateFormat.format(new Date(budget.getDate())));

            if (budget.getPaymentMethod() != null && !budget.getPaymentMethod().isEmpty()) {
                binding.tvPaymentMethod.setVisibility(View.VISIBLE);
                binding.tvPaymentMethod.setText(budget.getPaymentMethod());
            } else {
                binding.tvPaymentMethod.setVisibility(View.GONE);
            }

            // ✅ استخدام FormatUtils للتوحيد
            String amountText;
            int amountColor;
            if (budget.isIncome()) {
                amountText = "+" + FormatUtils.formatCurrency(budget.getAmount());
                amountColor = ContextCompat.getColor(context, R.color.income_green);
            } else {
                amountText = "-" + FormatUtils.formatCurrency(budget.getAmount());
                amountColor = ContextCompat.getColor(context, R.color.expense_red);
            }
            binding.tvAmount.setText(amountText);
            binding.tvAmount.setTextColor(amountColor);

            int bgColor = budget.getCategoryColor() != 0
                    ? budget.getCategoryColor()
                    : ContextCompat.getColor(context, R.color.category_other);
            binding.viewCategoryBg.getBackground().setTint(bgColor);

            if (budget.getCategoryIcon() != null) {
                int iconRes = IconUtils.getIconResource(context, budget.getCategoryIcon());
                binding.ivCategoryIcon.setImageResource(iconRes != 0 ? iconRes : R.drawable.ic_other);
            } else {
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_other);
            }
            binding.ivCategoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_primary));

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onBudgetClick(budget);
            });
            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(budget);
            });
            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(budget);
            });
        }
    }
}