package com.smartwallet.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")

public class Category {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String icon;
    private int color;
    private int type; // 0 = Income, 1 = Expense
    @Ignore
    public Category() {
    }

    public Category(String name, String icon, int color, int type) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.type = type;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isIncome() {
        return type == 0;
    }

    public boolean isExpense() {
        return type == 1;
    }
}