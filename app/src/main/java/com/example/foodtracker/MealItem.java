package com.example.foodtracker;

public class MealItem {
    private String name, time;
    private int calories, logId;
    private long ean;

    public MealItem(String name, String time, int calories, long ean, int logId) {
        this.name = name;
        this.time = time;
        this.calories = calories;
        this.ean = ean;
        this.logId = logId;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getCalories() {
        return calories;
    }

    public long getEan() {
        return ean;
    }

    public int getLogId() {
        return logId;
    }
}
