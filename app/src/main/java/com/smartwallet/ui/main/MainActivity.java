package com.smartwallet.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.smartwallet.R;
import com.smartwallet.databinding.ActivityMainBinding;
import com.smartwallet.ui.budget.AddBudgetActivity;
import com.smartwallet.ui.budget.AddBudgetDialog;
import com.smartwallet.ui.budget.BudgetsFragment;
import com.smartwallet.ui.reminders.RemindersFragment;
import com.smartwallet.ui.report.ReportsFragment;
import com.smartwallet.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
        setupFab();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_budgets) {
                loadFragment(new BudgetsFragment());
                return true;
            } else if (itemId == R.id.nav_reports) {
                loadFragment(new ReportsFragment());
                return true;
            } else if (itemId == R.id.nav_reminders) {
                loadFragment(new RemindersFragment());
                return true;
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment());
                return true;
            }
            return false;
        });
    }

    private void setupFab1() {
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBudgetActivity.class);
            startActivity(intent);
        });
    }
    // استبدل دالة setupFab() بهذا:
    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            AddBudgetDialog dialog = new AddBudgetDialog();
            Bundle b = new Bundle(); b.putInt("type", 1); // الافتراضي مصروف
            dialog.setArguments(b);
            dialog.show(getSupportFragmentManager(), "AddBudget");
        });
    }
    private void loadFragment(Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void hideBottomNav() {
        binding.bottomNavigation.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.GONE);
    }

    public void showBottomNav() {
        binding.bottomNavigation.setVisibility(View.VISIBLE);
        binding.fabAdd.setVisibility(View.VISIBLE);
    }
}