package com.example.android.cdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class ShowClasses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private HashMap<String, ArrayList> classes = new HashMap<>();
    private TreeSet<Integer> allSemesters = new TreeSet<>();

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

    private void setSpinner()
    {
        Spinner spinner = (Spinner) findViewById(R.id.semesterSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.semesterChoices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String choice = parent.getItemAtPosition(pos).toString();

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.classesLayout);

        if (choice.equals("All Semesters"))
        {
            mainLayout.removeAllViews();
            printAllClasses();
        } else if (choice.equals("None"))
        {
            mainLayout.removeAllViews();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }

    private void printAllClasses()
    {
        showSpinner();

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.classesLayout);

        for (int semester : allSemesters)
        {
            printAllClassesInSemester(semester, mainLayout);
        }
    }

    private void showSpinner()
    {
        LinearLayout spinnerGroup = (LinearLayout) findViewById(R.id.semesterSpinnerGroup);
        spinnerGroup.setVisibility(View.VISIBLE);
    }

    private void printAllClassesInSemester(int semester, LinearLayout layout)
    {
        TextView heading = new TextView(this);

        heading.setGravity(Gravity.CENTER_HORIZONTAL);
        heading.setText("SEMESTER " + Integer.toString(semester));
        heading.setTextSize(26);
        heading.setPadding(16,16,16,16);

        layout.addView(heading);

        for (String nameOfClass : classes.keySet())
        {
            if (getSemester(nameOfClass) == semester)
            {
                printSingleClass(nameOfClass, layout);
            }
        }
    }

    private void printSingleClass(String nameOfClass, LinearLayout layout)
    {
        TextView textView = new TextView(this);

        String text = nameOfClass + " | " + getGrade(nameOfClass) + " | " +
                getWorkload(nameOfClass);

        textView.setText(text);
        textView.setTextSize(15);

        layout.addView(textView);
    }

    private void printCdR()
    {
        Intent intent = getIntent();

        Double cdr = intent.getDoubleExtra("CDR", 0);

        TextView textView = new TextView(this);

        textView.setText("Coeficiente de Rendimento\n" + cdr.toString());
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(16,16,16,16);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.showClassesMain);

        mainLayout.addView(textView);
    }

    private void printWarning()
    {
        TextView warning = new TextView(this);

        warning.setText("No classes were registered!");
        warning.setTextSize(16);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.showClassesMain);

        // Add the warning just after the heading.
        mainLayout.addView(warning, 1);
    }

    private int getWorkload(String nameOfClass)
    {
        // Check to see if that class is registered
        if (classes.containsKey((nameOfClass)))
        {
            Double value = (Double) classes.get(nameOfClass).get(AddClasses.WORKLOAD_INDEX);
            return value.intValue();
        } else
        {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    private double getGrade(String nameOfClass)
    {
        // Check to see if that class is registered
        if (classes.containsKey((nameOfClass)))
        {
            return (double) classes.get(nameOfClass).get(AddClasses.GRADE_INDEX);
        } else
        {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    private int getSemester(String nameOfClass)
    {
        // Check to see if that class is registered
        if (classes.containsKey((nameOfClass)))
        {
            Double semester = (Double) classes.get(nameOfClass).get(AddClasses.SEMESTER_INDEX);
            return semester.intValue();
        } else
        {
            makeToast(nameOfClass + "is not registered in the system!");
            return -1;
        }
    }

    public void makeToast(String text)
    {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
