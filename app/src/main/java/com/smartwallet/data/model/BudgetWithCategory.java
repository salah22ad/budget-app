package com.smartwallet.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * POJO يجمع Budget مع Category المرتبطة به عبر @Relation
 * يُستخدم لإعادة بيانات العملية مع اسم ولون وأيقونة الفئة تلقائياً
 */
public class BudgetWithCategory {

    @Embedded
    public Budget budget;

    @Relation(parentColumn = "categoryId", entityColumn = "id")
    public Category category;
}