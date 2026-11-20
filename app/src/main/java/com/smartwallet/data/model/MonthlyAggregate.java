package com.smartwallet.data.model;

public class MonthlyAggregate {
    public int type;        // 0 = Income, 1 = Expense
    public String month;    // "yyyy-MM"
    public double total;
}