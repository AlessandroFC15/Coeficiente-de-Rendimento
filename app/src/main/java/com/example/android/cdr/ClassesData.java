package com.example.android.cdr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alessandro on 03/04/2016.
 */
public class ClassesData extends SQLiteOpenHelper {
    public static final String COLUMN_NAME = "name_class";
    public static final String COLUMN_SEMESTER = "semester";
    public static final String COLUMN_WORKLOAD = "workload_hours";
    public static final String COLUMN_GRADE = "grade";

    private static final String DATABASE_NAME = "ClassesData";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Classes";
    private static final String CREATE_CLASSES_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME + " text, " +
                    COLUMN_SEMESTER + " integer, " +
                    COLUMN_WORKLOAD + " integer, " +
                    COLUMN_GRADE + " float);";

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
}
