package com.example.android.cdr;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeSet;

public class ShowClasses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // private HashMap<String, ArrayList> classes = new HashMap<>();
    private TreeSet<Integer> allSemesters = new TreeSet<>();
    ArrayList<String> choicesForSemesters = new ArrayList<>();

    private ClassesData classesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_classes);
        setTitle("Classes Registered");

        // Initiate database
        classesDB = new ClassesData(this);

        Intent intent = getIntent();

        allSemesters = (TreeSet) intent.getSerializableExtra("Semesters");

        setSpinner();

        if (classesDB.isClassesTableEmpty()) {
            printWarning();
        } else {
            printAllClasses();
            printCdR();
        }
    }

    // Methods related to the spinner

    private void setSpinner() {
        setChoicesForShowOfSemesters();

        Spinner spinner = (Spinner) findViewById(R.id.semesterSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, choicesForSemesters);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    private void setChoicesForShowOfSemesters() {
        // Default choices
        choicesForSemesters.add("All Semesters");

        for (int semester : allSemesters) {
            choicesForSemesters.add("Semester " + Integer.toString(semester));
        }

        choicesForSemesters.add("None");
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String choice = parent.getItemAtPosition(pos).toString();

        TableLayout table = (TableLayout) findViewById(R.id.classesTable);

        if (choice.equals("All Semesters")) {
            table.removeAllViews();
            printAllClasses();
        } else if (choice.equals("None")) {
            table.removeAllViews();
        } else {
            // If the choice isn't one of the two above, it means that the user has selected a
            // specific semester. Therefore, we need to know which semester was picked.

            int semester = getChoiceOfSemester(choice);

            table.removeAllViews();

            printAllClassesInSemester(semester);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }

    private int getChoiceOfSemester(String choice) {
        // Get the last 2 chars, given the range that a semester can be (1,16)
        String number = choice.substring(choice.length() - 2).trim();

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            makeToast("Sorry, we encountered an error!");
            return -1;
        }
    }

    private void printAllClasses() {
        TableLayout mainLayout = (TableLayout) findViewById(R.id.classesTable);

        Cursor cursor = classesDB.getClassesOfUser();

        try {
            while (cursor.moveToNext()) {
                String nameOfClass = cursor.getString(cursor.getColumnIndex(ClassesData.COLUMN_NAME));

                int grade = cursor.getInt(cursor.getColumnIndex(ClassesData.COLUMN_GRADE));

                int workload = cursor.getInt(cursor.getColumnIndex(ClassesData.COLUMN_WORKLOAD));

                printSingleClass(nameOfClass, grade, workload, mainLayout);
            }
        } finally {
            cursor.close();
        }
    }

    /*
     * The method below will print all classes in a given semester, followed by the CdR of that
     * particular semester
     */
    private void printAllClassesInSemester(int semester) {
        TableLayout table = (TableLayout) findViewById(R.id.classesTable);

        double totalPoints = 0, totalWorkload = 0;

        for (String nameOfClass : classesDB.getAllNamesOfClasses()) {
            if (getSemester(nameOfClass) == semester) {

                int workload = getWorkload(nameOfClass);

                totalPoints += workload * getGrade(nameOfClass);

                totalWorkload += workload;

                // printSingleClass(nameOfClass, table);
            }
        }

        double cdr = totalPoints/totalWorkload;

        printSemesterCdR(cdr, semester);
    }

    private void printSemesterCdR(double cdr, int semester)
    {
        TextView textView = new TextView(this);

        textView.setText("CdR (" + Integer.toString(semester) + "º Semestre) = " + Double.toString(cdr));
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(16, 16, 16, 16);

        TableLayout table = (TableLayout) findViewById(R.id.classesTable);

        table.addView(textView);
    }

    private void printSingleClass(String name, int grade, int workload, TableLayout table) {
        // Creation of the row that will hold the class
        TableRow wholeClass = new TableRow(this);

        // Add of nameOfClass cell
        TextView nameOfClass = new TextView(this);
        nameOfClass.setText(name);
        nameOfClass.setTextSize(16);
        nameOfClass.setWidth(getPixels(160));

        // Add of grade cell
        TextView textGrade = new TextView(this);
        textGrade.setText(getGradeString(grade));
        textGrade.setTextSize(16);
        textGrade.setWidth(getPixels(80));

        // Add of workload cell
        TextView textWorkload = new TextView(this);
        textWorkload.setText(Integer.toString(workload));
        textWorkload.setTextSize(16);
        textWorkload.setWidth(getPixels(80));


        wholeClass.addView(nameOfClass);
        wholeClass.addView(textGrade);
        wholeClass.addView(textWorkload);

        // Adding the row to the table
        TableLayout layout = (TableLayout) findViewById(R.id.classesTable);

        layout.addView(wholeClass);
    }

    private void printCdR() {

        float cdr = classesDB.getCDR();

        TextView textView = new TextView(this);

        textView.setText(String.format( "Coeficiente de Rendimento\n%.2f", cdr));
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(16, 16, 16, 16);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.showClassesMain);

        mainLayout.addView(textView);
    }

    private void printWarning() {
        clearScreen();

        TextView warning = new TextView(this);

        warning.setText("No classes were registered!");
        warning.setTextSize(16);
        warning.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.showClassesMain);

        // Add the warning just after the heading.
        mainLayout.addView(warning, 1);
    }

    private void clearScreen() {
        // Remove the table
        TableLayout table = (TableLayout) findViewById(R.id.wholeTable);
        table.setVisibility(View.GONE);

        // Remove the spinner
        LinearLayout spinnerGroup = (LinearLayout) findViewById(R.id.semesterSpinnerGroup);
        spinnerGroup.setVisibility(View.GONE);
    }

    private int getWorkload(String nameOfClass) {
        // Check to see if that class is registered
        if (classesDB.isClassRegistered(nameOfClass)) {
            return 68;
        } else {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    private double getGrade(String nameOfClass) {
        // Check to see if that class is registered
        if (classesDB.isClassRegistered(nameOfClass)) {
            return 10.0;
        } else {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    private String getGradeString(int grade)
    {
        if (grade != -1)
        {
            if (grade == 10)
            {
                return "EXC";
            } else if (grade == 7.5)
            {
                return "BOM";
            } else if (grade == 5)
            {
                return "REG";
            } else if (grade == 0)
            {
                return "INS";
            } else
            {
                makeToast("Problem grade to string conversion!");
                return null;
            }
        } else
        {
            return null;
        }
    }

    private int getSemester(String nameOfClass) {
        // Check to see if that class is registered
        if (classesDB.isClassRegistered(nameOfClass)) {
            return 1;
        } else {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private int getPixels(int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
