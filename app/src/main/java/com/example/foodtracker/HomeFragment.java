package com.example.foodtracker;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button next, prev;
    private MealAdapter mealAdapter;
    TextView tvDate, tvCalories, tvCarbs, tvProtein, tvFat;
    ArrayList<MealItem> mealItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.rvEntries);
        tvDate = getView().findViewById(R.id.tvDate);
        tvCalories = getView().findViewById(R.id.tvKaloriengesamt);
        tvCarbs = getView().findViewById(R.id.tvCarbs);
        tvProtein = getView().findViewById(R.id.tvProtein);
        tvFat = getView().findViewById(R.id.tvFat);

        view.findViewById(R.id.btnAddMeal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)).initiateScan();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        //buildRecyclerView("2021-06-18");
        buildCalendar();
    }

    private void buildCalendar() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dfAnzeige = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat dfVergleich = new SimpleDateFormat("yyyy-MM-dd");
        String datumAnzeige = dfAnzeige.format(c.getTime());
        String datumVergleich = dfVergleich.format(c.getTime());
        buildRecyclerView(datumVergleich);
        setSummary(datumVergleich);
        tvDate.setText(datumAnzeige);


        getView().findViewById(R.id.btnPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Vorherige", Toast.LENGTH_SHORT).show();
                c.add(Calendar.DATE, -1);
                String datumAnzeige = dfAnzeige.format(c.getTime());
                tvDate.setText(datumAnzeige);
                String datumVergleich = dfVergleich.format(c.getTime());
                buildRecyclerView(datumVergleich);
                setSummary(datumVergleich);
            }
        });

        getView().findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Nächste", Toast.LENGTH_SHORT).show();
                c.add(Calendar.DATE, 1);
                String datumAnzeige = dfAnzeige.format(c.getTime());
                tvDate.setText(datumAnzeige);
                String datumVergleich = dfVergleich.format(c.getTime());
                buildRecyclerView(datumVergleich);
                setSummary(datumVergleich);
            }
        });
    }

    private void setSummary(String datumVergleich) {
        DecimalFormat df = new DecimalFormat("###");

        Cursor cursor = MainActivity.DBreadable.rawQuery("SELECT coalesce(sum((l_amount/100)*f_energy),'0'), l_date FROM log INNER JOIN food ON log.f_ean=food.f_ean WHERE l_date LIKE '" + datumVergleich + "%' ORDER BY l_date desc", null);
        cursor.moveToFirst();
        tvCalories.setText(df.format(cursor.getDouble(0)) + " kcal");

        cursor = MainActivity.DBreadable.rawQuery("SELECT coalesce(sum((l_amount/100)*f_carbohydrates),'0'), l_date FROM log INNER JOIN food ON log.f_ean=food.f_ean WHERE l_date LIKE '" + datumVergleich + "%' ORDER BY l_date desc", null);
        cursor.moveToFirst();
        tvCarbs.setText(df.format(cursor.getDouble(0)) + "g");

        cursor = MainActivity.DBreadable.rawQuery("SELECT coalesce(sum((l_amount/100)*f_proteins),'0'), l_date FROM log INNER JOIN food ON log.f_ean=food.f_ean WHERE l_date LIKE '" + datumVergleich + "%' ORDER BY l_date desc", null);
        cursor.moveToFirst();
        tvProtein.setText(df.format(cursor.getDouble(0)) + "g");

        cursor = MainActivity.DBreadable.rawQuery("SELECT coalesce(sum((l_amount/100)*f_fat),'0'), l_date FROM log INNER JOIN food ON log.f_ean=food.f_ean WHERE l_date LIKE '" + datumVergleich + "%' ORDER BY l_date desc", null);
        cursor.moveToFirst();
        tvFat.setText(df.format(cursor.getDouble(0)) + "g");
    }

    public void buildRecyclerView(String datumVergleich) {
        String name, datum, selectedDatum;
        int kalorien, logId;
        long ean;

        mealItems = new ArrayList<>();
        Log.i("Info", "buildrecylclerView()");


        Cursor cursor = MainActivity.DBreadable.rawQuery("SELECT * FROM log INNER JOIN food ON log.f_ean=food.f_ean WHERE l_date LIKE '" + datumVergleich + "%' ORDER BY l_date DESC ", null);

        if (cursor.getCount() > 0) {
            Log.i("Database", "Die Tabelle hat" + cursor.getCount() + " Einträge");
            cursor.moveToFirst();
            Log.i("Info", cursor.getString(cursor.getColumnIndex("f_name")));


            while (!cursor.isAfterLast()) {
                name = cursor.getString(cursor.getColumnIndex("f_name"));
                datum = cursor.getString(cursor.getColumnIndex("l_date")).substring(10, 16);
                kalorien = (int) (Double.parseDouble(cursor.getString(cursor.getColumnIndex("f_energy"))) * (Double.parseDouble(cursor.getString(cursor.getColumnIndex("l_amount"))) / 100));
                ean = (long) Double.parseDouble(cursor.getString(cursor.getColumnIndex("f_ean")));
                logId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("l_id")));

                mealItems.add(new MealItem(name, datum, kalorien, ean, logId));

                cursor.moveToNext();
            }
        } else
            tvCalories.setText("0 kcal");


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        mealAdapter = new MealAdapter(mealItems);
        mealAdapter.setOnItemClickListener(new MealAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                //Toast.makeText(getContext(), "Feld " + position + " wurde berührt", Toast.LENGTH_SHORT).show();
                //EAN des zu ändernden Eintrags mitgeben
                startActivity(new Intent(getContext(), AddMeal.class)
                        .putExtra("type", "edit")
                        .putExtra("ean", String.valueOf(mealItems.get(position).getEan()))
                        .putExtra("logId", String.valueOf(mealItems.get(position).getLogId())));
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mealAdapter);
    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), AddMeal.class).putExtra("type", "add").putExtra("ean", result.getContents()));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
