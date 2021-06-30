package com.example.foodtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static DBHelper dbHelper;
    public static SQLiteDatabase DBwritable, DBreadable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(this, "Main wurde gestartet", Toast.LENGTH_SHORT).show();

        Log.i("Database", DBContract.createTableLog);
        Log.i("Database", DBContract.createTableFood);
        dbHelper = new DBHelper(getApplicationContext());
        DBwritable = dbHelper.getWritableDatabase();
        DBreadable = dbHelper.getReadableDatabase();
        //DBwritable.execSQL("insert into log values (null, 1564515156, 50, \"16.6.21\")");


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Toast.makeText(this, "Wieder auf Main", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "App wurde beendet", Toast.LENGTH_SHORT).show();
        dbHelper.close();
        DBreadable.close();
        DBwritable.close();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_home: selectedFragment = new HomeFragment(); break;
                case R.id.nav_stats: selectedFragment = new StatsFragment(); break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}