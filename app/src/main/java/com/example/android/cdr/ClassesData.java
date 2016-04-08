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

    private static final String DATABASE_NAME = "ClassesData";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";

    private static final String COMMA_SEP = ",";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_NAME = "name_class";
    public static final String COLUMN_SEMESTER = "semester";
    public static final String COLUMN_WORKLOAD = "workload_hours";
    public static final String COLUMN_GRADE = "grade";
    public static final String COLUMN_POINTS = "total_points";

    // Classes Table

    public static final String TABLE_NAME_CLASSES = "Classes";
    public static final String CREATE_CLASSES_TABLE =
            "CREATE TABLE " + TABLE_NAME_CLASSES + " (" +
                    COLUMN_USER + INT_TYPE + COMMA_SEP +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_SEMESTER + INT_TYPE + COMMA_SEP +
                    COLUMN_WORKLOAD + INT_TYPE + COMMA_SEP +
                    COLUMN_GRADE + REAL_TYPE + ");";

    public static final String DELETE_CLASSES_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_CLASSES;

    // Classes Users

    public static final String TABLE_NAME_USERS = "Users";
    public static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_NAME_USERS + " (" +
                    COLUMN_ID + INT_TYPE + " PRIMARY KEY," +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_WORKLOAD + INT_TYPE + COMMA_SEP +
                    COLUMN_POINTS + INT_TYPE + ");";

    public static final String DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_USERS;

    public int userID = 0;

    ClassesData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASSES_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DELETE_CLASSES_TABLE);
        db.execSQL(DELETE_USERS_TABLE);
        onCreate(db);
    }

    public void deleleAllTables()
    {
        deleteClassesTable();
        deleteUsersTable();
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteClassesTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME_CLASSES, null, null);
    }

    public void deleteUsersTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME_USERS, null, null);
    }

    public void addClass(String nameOfClass, int semester, int workload, double grade) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, nameOfClass);
        values.put(COLUMN_SEMESTER, semester);
        values.put(COLUMN_WORKLOAD, workload);
        values.put(COLUMN_GRADE, grade);

        // Insert the new row, returning the primary key value of the new row
        db.insert(TABLE_NAME_CLASSES, "null", values);
    }

    public ArrayList<String> getAllNamesOfClasses() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_NAME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                COLUMN_NAME + " DESC";

        Cursor cursor = db.query(
                TABLE_NAME_CLASSES,  // The table to query
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

    public Cursor getAllClassesData()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_NAME,
                COLUMN_SEMESTER,
                COLUMN_WORKLOAD,
                COLUMN_GRADE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                COLUMN_SEMESTER + " DESC";

        Cursor cursor = db.query(
                TABLE_NAME_CLASSES,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;
    }

    public boolean isClassesTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_CLASSES, null);
        if (cursor != null) {
            cursor.moveToFirst();                       // Always one row returned.
            return cursor.getInt(0) == 0;              // Zero count means empty table.
        }

        cursor.close();


        return true;
    }

    public boolean isClassRegistered(String nameOfClass)
    {
        ArrayList<String> allNameOfClasses = getAllNamesOfClasses();

        return allNameOfClasses.indexOf(nameOfClass) != -1;
    }
}

