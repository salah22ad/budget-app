package com.smartwallet.util;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.smartwallet.R;

public class PieMarker extends MarkerView {

    private final TextView tvTitle;
    private final TextView tvValue;

    public PieMarker(Context context) {
        super(context, R.layout.marker_pie);
        tvTitle = findViewById(R.id.tvMarkerTitle);
        tvValue = findViewById(R.id.tvMarkerValue);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // e.getData() يحتوي على النص الذي مررناه "الاسم: المبلغ"
        if (e.getData() != null) {
            String data = e.getData().toString();
            String[] parts = data.split("\\|");
            if (parts.length == 2) {
                tvTitle.setText(parts[0]);
                tvValue.setText(parts[1]);
            }
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 20f);
    }
}