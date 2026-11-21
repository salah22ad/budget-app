package com.smartwallet.data.model;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;

import com.google.firebase.firestore.PropertyName;

@Keep
public class MonthlyAggregate {
    
    @ColumnInfo(name = "type")
    @PropertyName("type")
    public int type;        // 0 = Income, 1 = Expense

    @ColumnInfo(name = "month")
    @PropertyName("month")
    public String month;    // "yyyy-MM"

    @ColumnInfo(name = "total")
    @PropertyName("total")
    public double total;
}
