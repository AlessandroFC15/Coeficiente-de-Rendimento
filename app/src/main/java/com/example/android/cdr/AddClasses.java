package com.example.android.cdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class AddClasses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private HashMap<String, ArrayList> classes = new HashMap<>();
    private TreeSet<Integer> allSemesters = new TreeSet<>();

    private int workloadHours = 0;
    private String grade = "EXC";

    private int totalWorkload = 0;
    private double totalPoints = 0;

    // Constants
    public static final int GRADE_INDEX = 0;
    public static final int WORKLOAD_INDEX = 1;
    public static final int SEMESTER_INDEX = 2;
    private static final int MAX_SEMESTER = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        setSpinner();
    }

    // Methods related to the spinner

    private void setSpinner()
    {
        Spinner spinner = (Spinner) findViewById(R.id.gradeSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gradeChoices, android.R.layout.simple_spinner_item);
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
        String item = parent.getItemAtPosition(pos).toString();

        grade = item;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        grade = "EXC";
    }

    // End of methods related to the spinner

    public void addClass(View view)
    {
        String nameOfClass = getNameOfClass();

        // Check to see if the name inputted is a valid name
        if (nameOfClass != "")
        {
            // Check to see if the class isn't already registered
            if (!classes.containsKey(nameOfClass))
            {
                if (workloadHours != 0)
                {
                    int semester = getSemester();

                    if (semester > 0)
                    {
                        allSemesters.add(semester);

                        double nota = gradeToNota(grade);

                        ArrayList<Double> values = new ArrayList<>(3);

                        values.add(GRADE_INDEX, nota);
                        values.add(WORKLOAD_INDEX, (double) workloadHours);
                        values.add(SEMESTER_INDEX, (double) semester);

                        classes.put(nameOfClass, values);

                        totalWorkload += workloadHours;

                        totalPoints += (nota * workloadHours);

                        makeToast(nameOfClass + " succesfully added!");

                        cleanInputField();
                    }
                } else
                {
                    makeToast("Select a workload!");
                }
            } else
            {
                makeToast("The class " + nameOfClass + " is already registered!");
            }
        }
    }

    // Helper Functions

    private String getNameOfClass()
    {
        EditText input = (EditText) findViewById(R.id.nameOfClass);

        // The function trim is used to exclude any additional whitespace.
        String name = input.getText().toString().trim();

        if (TextUtils.isEmpty(name))
        {
            input.setError("Text must not be empty!");
            return "";
        } else
        {
            return name;
        }
    }

    private int getSemester()
    {
        EditText editText = (EditText) findViewById(R.id.semester);

        // The function trim is used to exclude any additional whitespace.
        String input  = editText.getText().toString().trim();

        if (TextUtils.isEmpty(input))
        {
            editText.setError("Enter a valid semester!");
            return -1;
        } else
        {
            try {
                int value = Integer.parseInt(input);

                if (value <= 0 || value > MAX_SEMESTER)
                {
                    editText.setError("Semester must be between 1 and " + Integer.toString(MAX_SEMESTER));
                    return -1;
                }

                return value;
            } catch (NumberFormatException e) {
                editText.setError("Enter a valid semester!");
                return -1;
            }
        }
    }

    private void cleanInputField()
    {
        EditText input = (EditText) findViewById(R.id.nameOfClass);
        input.setText("");

        RadioGroup workloadInput = (RadioGroup) findViewById(R.id.workloadID);
        workloadInput.clearCheck();
        workloadHours = 0;

        EditText semester = (EditText) findViewById(R.id.semester);
        semester.setText("");
    }

    // Helper function

    public void makeToast(String text)
    {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private double gradeToNota(String grade)
    {
        switch (grade)
        {
            case "EXC":
                return 10;
            case "BOM":
                return 7.5;
            case "REG":
                return 5;
            case "INS":
                return 0;
            default:
                return 0;
        }
    }

    // Function related to the radio buttons.

    public void selectWorkload(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId())
        {
            case R.id.id34hours:
                if (checked) {
                    workloadHours = 34;
                    break;
                }
            case R.id.id68hours:
                if (checked)
                {
                    workloadHours = 68;
                    break;
                }
        }
    }

    public void changeActivity(View view)
    {
        Intent intent = new Intent(this, ShowClasses.class);

        intent.putExtra("Classes", classes);

        intent.putExtra("CDR", totalPoints/totalWorkload);

        intent.putExtra("Semesters", allSemesters);

        startActivity(intent);
    }
}

