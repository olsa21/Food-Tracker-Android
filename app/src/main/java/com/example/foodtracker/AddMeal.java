package com.example.foodtracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.foodtracker.DownloadImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AddMeal extends AppCompatActivity {
    String type, ean, logId;
    TextView brand, prodName, cal1, cal2, fat1, fat2, protein1, protein2, carbs1, carbs2, fatSatured, fatNotSatured, sugar, salt;
    Button add, delete;
    EditText menge;
    Spinner spinner;
    ImageView image;
    JSONObject product, nutrition;
    int faktor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        type = getIntent().getStringExtra("type");
        ean = getIntent().getStringExtra("ean");
        logId = getIntent().getStringExtra("logId");

        add = findViewById(R.id.btnAddMeal);
        delete = findViewById(R.id.btnDelete);

        brand = findViewById(R.id.tvBrand);
        prodName = findViewById(R.id.tvProductName);
        cal1 = findViewById(R.id.tvCalValue1);
        cal2 = findViewById(R.id.tvCalValue2);
        fat1 = findViewById(R.id.tvFatValue1);
        fat2 = findViewById(R.id.tvFatValue2);
        protein1 = findViewById(R.id.tvProteinValue1);
        protein2 = findViewById(R.id.tvProteinValue2);
        carbs1 = findViewById(R.id.tvCarbsValue1);
        carbs2 = findViewById(R.id.tvCarbsValue2);
        fatSatured = findViewById(R.id.tvFatSaturedValue);
        fatNotSatured = findViewById(R.id.tvFatNotSaturedValue);
        sugar = findViewById(R.id.tvSugarValue);
        salt = findViewById(R.id.tvSaltValue);

        image = findViewById(R.id.imageView);

        menge = findViewById(R.id.etMenge);

        spinner = findViewById(R.id.spinner);

        if (type.equals("edit")) {
            delete.setVisibility(View.VISIBLE);
            add.setText("Ändern");
            //Toast.makeText(this, "Die ID ist: " + logId  , Toast.LENGTH_SHORT).show();
            image.setVisibility(View.GONE);
            product = new JSONObject();
            nutrition = new JSONObject();
            //Daten aus Datenbank holen where id = ean
            //z.B.
            try {
                Cursor cursor = MainActivity.DBreadable.rawQuery("SELECT * FROM food WHERE f_ean = " + ean, null);
                cursor.moveToFirst();

                product.put("brands", cursor.getString(cursor.getColumnIndex("f_brand")));
                product.put("product_name_de", cursor.getString(cursor.getColumnIndex("f_name")));
                product.put("ean", cursor.getString(cursor.getColumnIndex("f_ean")));

                nutrition.put("energy-kcal_100g", cursor.getString(cursor.getColumnIndex("f_energy")));
                nutrition.put("fat_100g", cursor.getString(cursor.getColumnIndex("f_fat")));
                nutrition.put("saturated-fat_100g", cursor.getString(cursor.getColumnIndex("f_fat_satured")));
                nutrition.put("carbohydrates_100g", cursor.getString(cursor.getColumnIndex("f_carbohydrates")));
                nutrition.put("sugars_100g", cursor.getString(cursor.getColumnIndex("f_sugar")));
                nutrition.put("proteins_100g", cursor.getString(cursor.getColumnIndex("f_proteins")));
                nutrition.put("salt_100g", cursor.getString(cursor.getColumnIndex("f_salt")));
                product.put("nutriments", nutrition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Daten anzeigen
        } else {
            //Daten herunterladen
            //Daten anzeigen
            try {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                    product = new JSONObject(downloadProduct(getIntent().getStringExtra("ean"))).getJSONObject("product");
                    product.put("ean", getIntent().getStringExtra("ean"));
                    nutrition = product.getJSONObject("nutriments");
                    checkProductForNull();
                    image.setImageBitmap(new DownloadImage().execute(product.getString("image_front_url")).get());
                } else {
                    Toast.makeText(this, "Kein Internet", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        menge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (!menge.getText().toString().isEmpty())
                        setNutritionText(Double.parseDouble(menge.getText().toString()) / 100);
                    else
                        setNutritionText(Double.parseDouble("0"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClicked();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClicked();
            }
        });

        //Spinner
        ArrayList<String> sizeList = new ArrayList<>();
        sizeList.add("Gramm");
        sizeList.add("Portion (15g)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizeList.toArray(new String[sizeList.size()]));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(AddMeal.this, "selected", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "CLICK", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                if (spinner.getSelectedItem().toString() == sizeList.get(0)) {
                    faktor = 1;
                    menge.setText("100");
                } else {
                    faktor = 15;
                    menge.setText("1");
                }
                setNutritionText(Double.parseDouble(menge.getText().toString()) / 100);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //buildObject(1);
    }

    private void checkProductForNull() {
        boolean vollstaendig = true;
        ArrayList<String> contentNutrition = new ArrayList<>();
        contentNutrition.add("energy-kcal_100g");
        contentNutrition.add("fat_100g");
        contentNutrition.add("saturated-fat_100g");
        contentNutrition.add("carbohydrates_100g");
        contentNutrition.add("sugars_100g");
        contentNutrition.add("proteins_100g");
        contentNutrition.add("salt_100g");

        ArrayList<String> contentProduct = new ArrayList<>();
        contentProduct.add("brands");
        contentProduct.add("product_name_de");

        for (int i = 0; i < contentProduct.size(); i++) {
            if (product.optString(contentProduct.get(i)).isEmpty()) {
                vollstaendig = false;
                try {
                    product.put(contentProduct.get(i), "0");
                    Log.i("check", contentProduct.get(i) + ": " + product.getString(contentNutrition.get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < contentNutrition.size(); i++) {
            if (nutrition.optString(contentNutrition.get(i)).isEmpty()) {
                vollstaendig = false;
                try {
                    nutrition.put(contentNutrition.get(i), "0");
                    Log.i("check", contentNutrition.get(i) + ": " + nutrition.getString(contentNutrition.get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!vollstaendig)
            Toast.makeText(this, "Angaben nicht vollständig", Toast.LENGTH_LONG).show();
    }


    private String downloadProduct(String ean) {
        String ergebnis = "";
        Downloader downloader = new Downloader();

        try {
            String url = "https://de.openfoodfacts.org/api/v0/product/" + ean + "?fields=brands,product_name_de,nutriments,image_front_url";
            Log.i("Info", "URL: " + url);
            ergebnis = downloader.execute(url).get();
            Log.i("Info", "Ergebnis:" + ergebnis);
            return ergebnis;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setNutritionText(double menge) {
        DecimalFormat df = new DecimalFormat("###.##");
        try {

            //String imageLink = object.getString("image_nutrition_url");
            //String nutriments = object.getJSONObject("nutriments").getString("salt_100g");
            String nutriments = product.getJSONObject("nutriments").getString("energy-kcal_100g");

            brand.setText(product.getString("brands"));
            prodName.setText(product.getString("product_name_de"));

            cal1.setText(df.format(Double.parseDouble(nutrition.getString("energy-kcal_100g")) * menge * faktor) + " kcal");
            cal2.setText(df.format(Double.parseDouble(nutrition.getString("energy-kcal_100g")) * menge * faktor) + " kcal");

            fat1.setText(df.format(Double.parseDouble(nutrition.getString("fat_100g")) * menge * faktor) + " g");
            fat2.setText(df.format(Double.parseDouble(nutrition.getString("fat_100g")) * menge * faktor) + " g");
            fatSatured.setText(df.format(Double.parseDouble(nutrition.getString("saturated-fat_100g")) * menge * faktor) + " g");
            fatNotSatured.setText(df.format(Double.parseDouble(nutrition.getString("saturated-fat_100g")) * menge * faktor) + " g");

            carbs1.setText(df.format(Double.parseDouble(nutrition.getString("carbohydrates_100g")) * menge * faktor) + " g");
            carbs2.setText(df.format(Double.parseDouble(nutrition.getString("carbohydrates_100g")) * menge * faktor) + " g");
            sugar.setText(df.format(Double.parseDouble(nutrition.getString("sugars_100g")) * menge * faktor) + " g");

            protein1.setText(df.format(Double.parseDouble(nutrition.getString("proteins_100g")) * menge * faktor) + " g");
            protein2.setText(df.format(Double.parseDouble(nutrition.getString("proteins_100g")) * menge * faktor) + " g");
            salt.setText(df.format(Double.parseDouble(nutrition.getString("salt_100g")) * menge * faktor) + " g");
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            //add.setAlpha(.5f);
            //add.setClickable(false);
        }
    }

    public void onAddClicked() {
        if (!menge.getText().toString().equals("")) {
            if (type.equals("add")) {
                if (!menge.getText().toString().equals("0")) {

                    //SQLiteDatabase db = MainActivity.DBwritable;
                    String sqlFood = MainActivity.dbHelper.buildSqlFoodInsert(product);
                    Log.i("Database", sqlFood);
                    String sqlLog = MainActivity.dbHelper.buildSqlLogInsert(ean, Float.parseFloat(menge.getText().toString()) * faktor);
                    Log.i("Database", sqlLog);


                    MainActivity.DBwritable.execSQL(sqlFood);
                    MainActivity.DBwritable.execSQL(sqlLog);

                    finish();
                }
            } else {

                if (!menge.getText().toString().equals("0")) {
                    double amount = Double.parseDouble(menge.getText().toString()) * faktor;
                    String sqlUpdate = "UPDATE log SET l_amount = " + amount + " WHERE l_id = " + logId;
                    Log.i("Database", sqlUpdate);
                    MainActivity.DBwritable.execSQL(sqlUpdate);
                    finish();
                    //UPDATE log SET amount = menge.getText WHERE l_id = l_id
                    //eventuell wenn einzige Eintrag food Einrag löschen
                } else {
                    onDeleteClicked();
                }
            }
        }
    }

    public void onDeleteClicked() {
        //Delete from log where ean = ean
        String sqlDelete = "DELETE FROM log WHERE l_id = " + logId;
        Log.i("Database", sqlDelete);
        MainActivity.DBwritable.execSQL(sqlDelete);
        finish();
    }
}