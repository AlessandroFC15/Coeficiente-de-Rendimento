package com.example.android.cdr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Alessandro on 03/04/2016.
 */
public class ClassesData extends SQLiteOpenHelper {
    public static final String COLUMN_NAME = "name_class";
    public static final String COLUMN_SEMESTER = "semester";
    public static final String COLUMN_WORKLOAD = "workload_hours";
    public static final String COLUMN_GRADE = "grade";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";

    private static final String COMMA_SEP = ",";

    private static final String DATABASE_NAME = "ClassesData";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Classes";
    private static final String CREATE_CLASSES_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_SEMESTER + INT_TYPE + COMMA_SEP +
                    COLUMN_WORKLOAD + INT_TYPE + COMMA_SEP +
                    COLUMN_GRADE + REAL_TYPE + ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    ClassesData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASSES_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addClass(String nameOfClass, int semester, int workload, double grade)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, nameOfClass);
        values.put(COLUMN_SEMESTER, semester);
        values.put(COLUMN_WORKLOAD, workload);
        values.put(COLUMN_GRADE, grade);

        // Insert the new row, returning the primary key value of the new row
        db.insert(TABLE_NAME, "null", values);
    }

    public ArrayList<String> getAllNamesOfClasses()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_NAME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                COLUMN_NAME + " DESC";

        Cursor cursor = db.query(
                TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList<String> allNames = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                String nameOfClass = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

                allNames.add(nameOfClass);
            }
        } finally {
            cursor.close();
        }

        return allNames;
    }
}
