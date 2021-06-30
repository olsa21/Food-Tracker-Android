package com.example.foodtracker;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StatsFragment extends Fragment {
    TextView date;
    Cursor cursor[] = new Cursor[7];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        buildChartNew();

    }

    private void getCalories() {
        String date;

        for (int i = 0; i < 7; i++) {
            date = getDateBeforeToday(-i);
            String sql = "SELECT coalesce(sum((l_amount/100)*f_energy),'0') as kcal, l_date FROM log INNER JOIN food ON log.f_ean=food.f_ean WHERE l_date LIKE '" + date + "%'";
            cursor[i] = MainActivity.DBreadable.rawQuery(sql, null);
            Log.i("Stats", sql);
        }
    }

    private void buildChartNew() {
        getCalories();
        BarChart chart;

        chart = getView().findViewById(R.id.diagramm);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);

        BarDataSet[] set = new BarDataSet[7];

        List<BarEntry>[] barEntries = new List[7];
        Arrays.setAll(barEntries, element -> new ArrayList<>());

        for (int i = 0; i < 7; i++) {
            cursor[i].moveToFirst();
            int kcal = (int) Double.parseDouble(cursor[i].getString(0));

            barEntries[i].add(new BarEntry(i, kcal));
            SimpleDateFormat df = new SimpleDateFormat("EE");
            Calendar c = Calendar.getInstance();
            set[i] = new BarDataSet(barEntries[i], getDayBeforeToday(-i));
        }

        set[0].setColor(Color.rgb(230, 38, 0));
        set[1].setColor(Color.rgb(230, 0, 115));
        set[2].setColor(Color.rgb(230, 153, 0));
        set[3].setColor(Color.rgb(0, 230, 230));
        set[4].setColor(Color.rgb(102, 0, 51));
        set[5].setColor(Color.rgb(0, 0, 128));
        set[6].setColor(Color.rgb(0, 128, 43));

        BarData barData = new BarData(set[0], set[1], set[2], set[3], set[4], set[5], set[6]);

        chart.getAxisRight().setEnabled(false);

        chart.setData(barData);
    }

    private String getDateBeforeToday(int shift) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dfVergleich = new SimpleDateFormat("yyyy-MM-dd");
        c.add(Calendar.DATE, shift);
        String datumVergleich = dfVergleich.format(c.getTime());

        return datumVergleich;
    }

    private String getDayBeforeToday(int shift) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dfVergleich = new SimpleDateFormat("EE");
        c.add(Calendar.DATE, shift);
        String wochentag = dfVergleich.format(c.getTime());

        return wochentag;
    }
}
