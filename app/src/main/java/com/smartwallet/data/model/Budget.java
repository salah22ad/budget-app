package com.smartwallet.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private int type; // 0 = Income, 1 = Expense
    private double amount;
    private long categoryId;
    private long date;
    private String note;
    private String paymentMethod;
    private long createdAt;

    // Non-persisted fields for UI (populated via JOIN with categories)
    @Ignore
    private String categoryName;
    @Ignore
    private String categoryIcon;
    @Ignore
    private int categoryColor;

    public Budget() {
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }

    public int getCategoryColor() { return categoryColor; }
    public void setCategoryColor(int categoryColor) { this.categoryColor = categoryColor; }

    public boolean isIncome() { return type == 0; }
    public boolean isExpense() { return type == 1; }
}