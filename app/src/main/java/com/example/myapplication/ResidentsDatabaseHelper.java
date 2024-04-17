package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ResidentsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ResidentsDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RESIDENTS = "residents";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AGE = "age";

    public ResidentsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESIDENTS_TABLE = "CREATE TABLE " + TABLE_RESIDENTS +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_AGE + " INTEGER" +
                ")";
        db.execSQL(CREATE_RESIDENTS_TABLE);
    }

    public void deleteResident(int residentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESIDENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(residentId)});
        db.close();
    }

    public void updateResident(Resident resident) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, resident.getName());
        values.put(COLUMN_AGE, resident.getAge());
        db.update(TABLE_RESIDENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(resident.getId())});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESIDENTS);
        // Create tables again
        onCreate(db);
    }

    public long addResident(Resident resident) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, resident.getName());
        values.put(COLUMN_AGE, resident.getAge());
        long id = db.insert(TABLE_RESIDENTS, null, values);
        db.close();
        return id;
    }

    public List<Resident> getAllResidents() {
        List<Resident> residents = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESIDENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int ageIndex = cursor.getColumnIndex(COLUMN_AGE);

                if (idIndex != -1 && nameIndex != -1 && ageIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    int age = cursor.getInt(ageIndex);
                    Resident resident = new Resident(id, name, age);
                    residents.add(resident);
                } else {
                    // Handle the case where one or more columns are not found
                }
            } while (cursor.moveToNext());
        }

        // Close cursor and database connection
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return residents;
    }
}
