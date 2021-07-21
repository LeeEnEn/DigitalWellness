package com.example.digitalwellness;

import android.app.Activity;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MyCharts {
    private final Activity context;
    private BarChart stepChart;
    private BarChart screenChart;

    public MyCharts(Activity context) {
        this.context = context;
    }

    public void showStepGraph(ArrayList<BarEntry> values, String[] axis) {
        stepChart = (BarChart) context.findViewById(R.id.step_chart);

        // Disable all zooming.
        stepChart.setScaleEnabled(false);

        // Disable legend.
        stepChart.getLegend().setEnabled(false);

        // Remove grid lines on the x-axis.
        XAxis xAxis = stepChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        stepChart.getAxisLeft().setDrawGridLines(false);

        // Remove grid lines on the right side.
        stepChart.getAxisRight().setEnabled(false);

        // Set animation duration for y-axis.
        stepChart.animateY(1000);

        // Load data.
        BarDataSet dataSet = new BarDataSet(values, null);

        // Remove chart description.
        stepChart.getDescription().setEnabled(false);

        // Set color
        dataSet.setColors(ColorTemplate.getHoloBlue());

        // x-axis data
        BarData data = new BarData(dataSet);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(axis));
        stepChart.setDragEnabled(false);
        stepChart.setData(data);
        stepChart.notifyDataSetChanged();
        stepChart.invalidate();
    }

    public void showScreenGraph (ArrayList<BarEntry> values, String[] axis) {
        screenChart = (BarChart) context.findViewById(R.id.screenchart);
        Log.e("LOG", "Screen graph entered");
        Log.e("LOG", values.toString());

        // Disable all zooming.
        screenChart.setScaleEnabled(false);

        // Disable legend.
        screenChart.getLegend().setEnabled(false);

        // Remove grid lines on the x-axis.
        XAxis xAxis = screenChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        screenChart.getAxisLeft().setDrawGridLines(false);

        // Remove grid lines on the right side.
        screenChart.getAxisRight().setEnabled(false);

        // Set animation duration for y-axis.
        screenChart.animateY(1000);

        // Set x-axis value.

        xAxis.setValueFormatter(new IndexAxisValueFormatter(axis));

        // Load data.
        BarDataSet dataSet = new BarDataSet(values, null);

        // Remove chart description.
        screenChart.getDescription().setEnabled(false);

        // Set color
        dataSet.setColors(ColorTemplate.getHoloBlue());

        // x-axis data
        BarData data = new BarData(dataSet);
        screenChart.setData(data);
        screenChart.setFitBars(true);
        screenChart.setVisibleXRangeMaximum(7);
        screenChart.setDragEnabled(false);
    }
}
