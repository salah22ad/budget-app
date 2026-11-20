package com.smartwallet.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwallet.databinding.ItemCategorySummaryBinding;
import com.smartwallet.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.CategoryViewHolder> {

    private final List<CategorySummary> categories = new ArrayList<>();

    public static class CategorySummary {
        public String name;
        public int color;
        public double amount;
        public float percentage;

        public CategorySummary(String name, int color, double amount, float percentage) {
            this.name = name;
            this.color = color;
            this.amount = amount;
            this.percentage = percentage;
        }
    }

    public CategorySummaryAdapter(Context context) { /* context محفوظ للـ tint إن لزم */ }

    public void setCategories(List<CategorySummary> newCategories) {
        categories.clear();
        if (newCategories != null) categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategorySummaryBinding binding = ItemCategorySummaryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() { return categories.size(); }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategorySummaryBinding binding;

        CategoryViewHolder(ItemCategorySummaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CategorySummary category) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(8f);
            drawable.setColor(category.color);
            binding.viewColorIndicator.setBackground(drawable);

            binding.tvCategoryName.setText(category.name);

            // ✅ استخدام FormatUtils
            binding.tvAmount.setText(FormatUtils.formatCurrency(category.amount));
            binding.tvPercentage.setText(FormatUtils.formatPercentage(category.percentage));
        }
    }
}