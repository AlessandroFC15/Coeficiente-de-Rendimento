package com.example.android.cdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class ShowClasses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private HashMap<String, ArrayList> classes = new HashMap<>();
    private TreeSet<Integer> allSemesters = new TreeSet<>();
    ArrayList<String> choicesForSemesters = new ArrayList<>();

    // Constants
    private static final int NONE = 0;
    private static final int ALL_SEMESTERS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_classes);

        Intent intent = getIntent();

        classes = (HashMap) intent.getSerializableExtra("Classes");

        allSemesters = (TreeSet) intent.getSerializableExtra("Semesters");

        setSpinner();

        if (classes.isEmpty()) {
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

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }

    private void printAllClasses() {
        TableLayout mainLayout = (TableLayout) findViewById(R.id.classesTable);

        for (String nameOfClass : classes.keySet()) {
            printSingleClass(nameOfClass, mainLayout);
        }
    }

    private void printAllClassesInSemester(int semester) {
        TableLayout table = (TableLayout) findViewById(R.id.classesTable);

        for (String nameOfClass : classes.keySet()) {
            if (getSemester(nameOfClass) == semester) {
                printSingleClass(nameOfClass, table);
            }
        }
    }

    private void printSingleClass(String name, TableLayout table) {
        // Creation of the row that will hold the class
        TableRow wholeClass = new TableRow(this);

        // Creating LayoutParams to implement the correct weight
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 2;

        // Add of nameOfClass cell
        TextView nameOfClass = new TextView(this);
        nameOfClass.setText(name);
        nameOfClass.setTextSize(15);
        //nameOfClass.setLayoutParams(params);

        wholeClass.addView(nameOfClass);

        // Changing weight to 1 for the other 2 cells
        params.weight = 1;

        // Add of grade cell
        TextView grade = new TextView(this);
        grade.setText(Double.toString(getGrade(name)));
        //grade.setLayoutParams(params);

        wholeClass.addView(grade);

        // Add of workload cell
        TextView workload = new TextView(this);
        workload.setText(Integer.toString(getWorkload(name)));
        //workload.setLayoutParams(params);

        wholeClass.addView(workload);

        // Adding the row to the table
        TableLayout layout = (TableLayout) findViewById(R.id.classesTable);

        layout.addView(wholeClass);
    }

    private void printCdR() {
        Intent intent = getIntent();

        Double cdr = intent.getDoubleExtra("CDR", 0);

        TextView textView = new TextView(this);

        textView.setText("Coeficiente de Rendimento\n" + cdr.toString());
        textView.setTextSize(20);
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
        if (classes.containsKey((nameOfClass))) {
            Double value = (Double) classes.get(nameOfClass).get(AddClasses.WORKLOAD_INDEX);
            return value.intValue();
        } else {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    private double getGrade(String nameOfClass) {
        // Check to see if that class is registered
        if (classes.containsKey((nameOfClass))) {
            return (double) classes.get(nameOfClass).get(AddClasses.GRADE_INDEX);
        } else {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    private int getSemester(String nameOfClass) {
        // Check to see if that class is registered
        if (classes.containsKey((nameOfClass))) {
            Double semester = (Double) classes.get(nameOfClass).get(AddClasses.SEMESTER_INDEX);
            return semester.intValue();
        } else {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
