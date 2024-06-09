package com.example.tasktracker;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "TaskTracker.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTE = "note";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_NOTE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addOrUpdateTask(String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_NOTE, note);

        String whereClause = COLUMN_DATE + "=?";
        String[] whereArgs = {date};

        Cursor cursor = db.query(TABLE_NAME, null, whereClause, whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            // Запись уже существует, обновляем её
            db.update(TABLE_NAME, values, whereClause, whereArgs);
        } else {
            // Запись не существует, вставляем новую
            db.insert(TABLE_NAME, null, values);
        }

        cursor.close();
        db.close();
    }


    public String getNoteByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NOTE + " FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{date});
        if (cursor.moveToFirst()) {
            String note = cursor.getString(0);
            cursor.close();
            db.close();
            return note;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public Date[] getAllDates() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_DATE + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        List<Date> dateList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_DATE);
                if (columnIndex != -1) {
                    long dateMillis = cursor.getLong(columnIndex);
                    Date date = new Date(dateMillis);
                    dateList.add(date);
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        // Преобразуем список в массив
        Date[] datesArray = new Date[dateList.size()];
        datesArray = dateList.toArray(datesArray);

        return datesArray;
    }
}
