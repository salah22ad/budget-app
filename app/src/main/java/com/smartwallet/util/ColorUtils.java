package com.smartwallet.util;

import android.content.Context;
import android.graphics.Color;

import com.smartwallet.R;

public class ColorUtils {

    public static int parseColor(String colorString) {
        try {
            return Color.parseColor(colorString);
        } catch (Exception e) {
            return Color.GRAY;
        }
    }

    public static int darkenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) * factor);
        int g = (int) (Color.green(color) * factor);
        int b = (int) (Color.blue(color) * factor);
        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    public static int lightenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = (int) ((Color.red(color) * (1 - factor)) + 255 * factor);
        int g = (int) ((Color.green(color) * (1 - factor)) + 255 * factor);
        int b = (int) ((Color.blue(color) * (1 - factor)) + 255 * factor);
        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    public static int getIncomeColor(Context context) {
        return context.getResources().getColor(R.color.income_green, context.getTheme());
    }

    public static int getExpenseColor(Context context) {
        return context.getResources().getColor(R.color.expense_red, context.getTheme());
    }

    public static int getPrimaryColor(Context context) {
        return context.getResources().getColor(R.color.primary, context.getTheme());
    }

    public static int getBackgroundColor(Context context) {
        return context.getResources().getColor(R.color.background, context.getTheme());
    }

    public static int getCardColor(Context context) {
        return context.getResources().getColor(R.color.white, context.getTheme());
    }

    public static int getTextPrimaryColor(Context context) {
        return context.getResources().getColor(R.color.text_primary, context.getTheme());
    }

    public static int getTextSecondaryColor(Context context) {
        return context.getResources().getColor(R.color.text_secondary, context.getTheme());
    }
}