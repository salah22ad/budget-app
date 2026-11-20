package com.smartwallet.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smartwallet.data.model.Category;

import java.util.List;

import androidx.lifecycle.LiveData;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(long id);

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    LiveData<List<Category>> getCategoriesByType(int type);

    @Query("SELECT * FROM categories WHERE type = 0 ORDER BY name ASC")
    LiveData<List<Category>> getIncomeCategories();

    @Query("SELECT * FROM categories WHERE type = 1 ORDER BY name ASC")
    LiveData<List<Category>> getExpenseCategories();

    @Query("SELECT COUNT(*) FROM categories")
    int getCategoryCount();
}