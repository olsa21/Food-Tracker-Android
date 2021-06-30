package com.example.foodtracker;

import android.provider.BaseColumns;

public class DBContract {
    public static final String createTableFood =
            /*"CREATE TABLE " + FoodEntry.TABLE_NAME + " (" +
                    FoodEntry.COLUMN_F_EAN + " INTEGER NOT NULL," +
                    FoodEntry.COLUMN_ENERGY + " INTEGER," +
                    FoodEntry.COLUMN_FAT + " REAL," +
                    FoodEntry.COLUMN_FAT_SATURED + " REAL," +
                    FoodEntry.COLUMN_CARBOHYDRATES + " REAL," +
                    FoodEntry.COLUMN_SUGAR + " REAL," +
                    FoodEntry.COLUMN_PROTEINS + " REAL," +
                    FoodEntry.COLUMN_SALT + " REAL," +
                    " PRIMARY KEY (" + FoodEntry.COLUMN_F_EAN + ");";*/
        "CREATE TABLE IF NOT EXISTS \"food\" (\n" +
                "\t\"f_ean\"\tINTEGER NOT NULL,\n" +
                "\t\"f_brand\"\tINTEGER,\n" +
                "\t\"f_name\"\tINTEGER,\n" +
                "\t\"f_energy\"\tTEXT,\n" +
                "\t\"f_fat\"\tNUMERIC,\n" +
                "\t\"f_fat_satured\"\tREAL,\n" +
                "\t\"f_carbohydrates\"\tREAL,\n" +
                "\t\"f_sugar\"\tREAL,\n" +
                "\t\"f_proteins\"\tREAL,\n" +
                "\t\"f_salt\"\tREAL,\n" +
                "\tPRIMARY KEY(\"f_ean\")\n" +
                ");";

    public static final String createTableLog =
            "CREATE TABLE IF NOT EXISTS \"log\" (\n" +
                    "\t\"l_id\"\tINTEGER NOT NULL,\n" +
                    "\t\"f_ean\"\tINTEGER,\n" +
                    "\t\"l_amount\"\tREAL,\n" +
                    "\t\"l_date\"\tTEXT,\n" +
                    "\tPRIMARY KEY(\"l_id\" AUTOINCREMENT),\n" +
                    "\tFOREIGN KEY(\"f_ean\") REFERENCES \"food\"(\"f_ean\")\n" +
                    ");";

    public static final String deleteEntries = "DROP TABLE IF EXISTS " + FoodEntry.TABLE_NAME + ";" +
            "DROP TABLE IF EXISTS " + LogEntry.TABLE_NAME + ";";

    public static class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "log";
        public static final String COLUMN_ID = "l_id";
        public static final String COLUMN_F_EAN = "f_ean";
        public static final String COLUMN_AMOUNT = "l_amount";
        public static final String COLUMN_DATE = "l_date";
    }

    public static class FoodEntry implements BaseColumns {
        public static final String TABLE_NAME = "food";
        public static final String COLUMN_F_EAN = "f_ean";
        public static final String COLUMN_ENERGY = "f_energy";
        public static final String COLUMN_FAT = "f_fat";
        public static final String COLUMN_FAT_SATURED = "f_fat_satured";
        public static final String COLUMN_CARBOHYDRATES = "f_carbohydrates";
        public static final String COLUMN_SUGAR = "f_sugar";
        public static final String COLUMN_PROTEINS = "f_proteins";
        public static final String COLUMN_SALT = "f_salt";
    }


}
