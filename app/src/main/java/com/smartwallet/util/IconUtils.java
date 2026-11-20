package com.smartwallet.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.smartwallet.R;

public class IconUtils {

    public static Drawable getIcon(Context context, String iconName) {
        int resId = context.getResources().getIdentifier(
                iconName, "drawable", context.getPackageName());
        if (resId != 0) {
            return ContextCompat.getDrawable(context, resId);
        }
        return ContextCompat.getDrawable(context, R.drawable.ic_other);
    }

    public static int getIconResource(Context context, String iconName) {
        return context.getResources().getIdentifier(
                iconName, "drawable", context.getPackageName());
    }

    // Icon names
    public static final String ICON_SALARY = "ic_salary";
    public static final String ICON_WORK = "ic_work";
    public static final String ICON_INVEST = "ic_invest";
    public static final String ICON_GIFT = "ic_gift";
    public static final String ICON_OTHER = "ic_other";

    public static final String ICON_FOOD = "ic_food";
    public static final String ICON_TRANSPORT = "ic_transport";
    public static final String ICON_HEALTH = "ic_health";
    public static final String ICON_EDUCATION = "ic_education";
    public static final String ICON_ENTERTAINMENT = "ic_entertainment";
    public static final String ICON_SHOPPING = "ic_shopping";
    public static final String ICON_BILLS = "ic_bills";
    public static final String ICON_HOUSE = "ic_house";

    public static final String ICON_GOAL = "ic_goal";
    public static final String ICON_REMINDER = "ic_reminder";
}