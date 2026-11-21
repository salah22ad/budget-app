package com.smartwallet.data.model;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.PropertyName;

@Keep
@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @PropertyName("id")
    private long id;

    @ColumnInfo(name = "type")
    @PropertyName("type")
    private int type; // 0 = Income, 1 = Expense

    @ColumnInfo(name = "amount")
    @PropertyName("amount")
    private double amount;

    @ColumnInfo(name = "categoryId")
    @PropertyName("categoryId")
    private long categoryId;

    @ColumnInfo(name = "date")
    @PropertyName("date")
    private long date;

    @ColumnInfo(name = "note")
    @PropertyName("note")
    private String note;

    @ColumnInfo(name = "paymentMethod")
    @PropertyName("paymentMethod")
    private String paymentMethod;

    @ColumnInfo(name = "createdAt")
    @PropertyName("createdAt")
    private long createdAt;

    // Non-persisted fields for UI (populated via JOIN with categories)
    @Ignore
    @PropertyName("categoryName")
    private String categoryName;
    
    @Ignore
    @PropertyName("categoryIcon")
    private String categoryIcon;
    
    @Ignore
    @PropertyName("categoryColor")
    private int categoryColor;

    public Budget() {
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    @PropertyName("id")
    public long getId() { return id; }
    @PropertyName("id")
    public void setId(long id) { this.id = id; }

    @PropertyName("type")
    public int getType() { return type; }
    @PropertyName("type")
    public void setType(int type) { this.type = type; }

    @PropertyName("amount")
    public double getAmount() { return amount; }
    @PropertyName("amount")
    public void setAmount(double amount) { this.amount = amount; }

    @PropertyName("categoryId")
    public long getCategoryId() { return categoryId; }
    @PropertyName("categoryId")
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    @PropertyName("date")
    public long getDate() { return date; }
    @PropertyName("date")
    public void setDate(long date) { this.date = date; }

    @PropertyName("note")
    public String getNote() { return note; }
    @PropertyName("note")
    public void setNote(String note) { this.note = note; }

    @PropertyName("paymentMethod")
    public String getPaymentMethod() { return paymentMethod; }
    @PropertyName("paymentMethod")
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    @PropertyName("createdAt")
    public long getCreatedAt() { return createdAt; }
    @PropertyName("createdAt")
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @PropertyName("categoryName")
    public String getCategoryName() { return categoryName; }
    @PropertyName("categoryName")
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @PropertyName("categoryIcon")
    public String getCategoryIcon() { return categoryIcon; }
    @PropertyName("categoryIcon")
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }

    @PropertyName("categoryColor")
    public int getCategoryColor() { return categoryColor; }
    @PropertyName("categoryColor")
    public void setCategoryColor(int categoryColor) { this.categoryColor = categoryColor; }

    public boolean isIncome() { return type == 0; }
    public boolean isExpense() { return type == 1; }
}
