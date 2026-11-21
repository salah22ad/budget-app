package com.smartwallet.data.model;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.PropertyName;

@Keep
@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @PropertyName("id")
    private long id;

    @ColumnInfo(name = "name")
    @PropertyName("name")
    private String name;

    @ColumnInfo(name = "icon")
    @PropertyName("icon")
    private String icon;

    @ColumnInfo(name = "color")
    @PropertyName("color")
    private int color;

    @ColumnInfo(name = "type")
    @PropertyName("type")
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
    @PropertyName("id")
    public long getId() { return id; }
    @PropertyName("id")
    public void setId(long id) { this.id = id; }

    @PropertyName("name")
    public String getName() { return name; }
    @PropertyName("name")
    public void setName(String name) { this.name = name; }

    @PropertyName("icon")
    public String getIcon() { return icon; }
    @PropertyName("icon")
    public void setIcon(String icon) { this.icon = icon; }

    @PropertyName("color")
    public int getColor() { return color; }
    @PropertyName("color")
    public void setColor(int color) { this.color = color; }

    @PropertyName("type")
    public int getType() { return type; }
    @PropertyName("type")
    public void setType(int type) { this.type = type; }

    public boolean isIncome() { return type == 0; }
    public boolean isExpense() { return type == 1; }
}
