package com.example.android.cdr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ClassesData extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ClassesData";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";

    private static final String COMMA_SEP = ",";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name_class";
    public static final String COLUMN_SEMESTER = "semester";
    public static final String COLUMN_WORKLOAD = "workload_hours";
    public static final String COLUMN_GRADE = "grade";
    public static final String COLUMN_POINTS = "total_points";

    // Classes Table

    public static final String TABLE_NAME_CLASSES = "Classes";
    public static final String CREATE_CLASSES_TABLE =
            "CREATE TABLE " + TABLE_NAME_CLASSES + " (" +
                    COLUMN_ID + INT_TYPE + COMMA_SEP +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_SEMESTER + INT_TYPE + COMMA_SEP +
                    COLUMN_WORKLOAD + INT_TYPE + COMMA_SEP +
                    COLUMN_GRADE + REAL_TYPE + ");";

    public static final String DELETE_CLASSES_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_CLASSES;

    // Users Table

    public static final String TABLE_NAME_USERS = "Users";
    public static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_NAME_USERS + " (" +
                    COLUMN_ID + INT_TYPE + " PRIMARY KEY," +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_WORKLOAD + INT_TYPE + COMMA_SEP +
                    COLUMN_POINTS + INT_TYPE + ");";

    public static final String DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_USERS;

    public int userID = 1;

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
        values.put(COLUMN_ID, userID);
        values.put(COLUMN_NAME, nameOfClass);
        values.put(COLUMN_SEMESTER, semester);
        values.put(COLUMN_WORKLOAD, workload);
        values.put(COLUMN_GRADE, grade);

        addClassToUsersTable((int) (workload * grade), workload);

        // Insert the new row, returning the primary key value of the new row
        db.insert(TABLE_NAME_CLASSES, "null", values);
    }

    private void addClassToUsersTable(int points, int workload)
    {
        // 1st Step = Get the old values
        SQLiteDatabase db = this.getReadableDatabase();
        int totalPoints = 0;
        float totalWorkload = 0;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_WORKLOAD,
                COLUMN_POINTS
        };

        String selection = COLUMN_ID + " LIKE ?";

        String selectionArgs[] = {Integer.toString(userID)};

        Cursor c = db.query(
                TABLE_NAME_USERS,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        try {
            while (c.moveToNext()) {
                totalWorkload = c.getInt(c.getColumnIndex(COLUMN_WORKLOAD));

                totalPoints = c.getInt(c.getColumnIndex(COLUMN_POINTS));
            }
        } finally {
            c.close();
        }

        // 2nd Step | Update the values

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORKLOAD, totalWorkload + workload);
        values.put(COLUMN_POINTS, totalPoints + points);

        db.update(TABLE_NAME_USERS,
                values,
                selection,
                selectionArgs);
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

    public float getCDR()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_WORKLOAD,
                COLUMN_POINTS
        };

        String selection = COLUMN_ID + " = ?";

        String selectionArgs[] = {Integer.toString(userID)};

        Cursor cursor = db.query(
                TABLE_NAME_USERS,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                            // The sort order
        );

        int points = 1 , workload = 1;

        try {
            while (cursor.moveToNext()) {
                points = cursor.getInt(cursor.getColumnIndex(ClassesData.COLUMN_POINTS));

                workload = cursor.getInt(cursor.getColumnIndex(ClassesData.COLUMN_WORKLOAD));
            }
        } finally {
            cursor.close();
        }

        return (float) points / workload;
    }

    public Cursor getClassesOfUser()
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

        String selection = COLUMN_ID + " LIKE ?";

        String selectionArgs[] = {Integer.toString(userID)};

        return db.query(
                TABLE_NAME_CLASSES,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
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

    public void addUser(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_WORKLOAD, 0);
        values.put(COLUMN_POINTS, 0);

        // Insert the new row, returning the primary key value of the new row
        db.insert(TABLE_NAME_USERS, "null", values);
    }

    public Cursor getAllUsersData()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_NAME_USERS, null);
    }
}

