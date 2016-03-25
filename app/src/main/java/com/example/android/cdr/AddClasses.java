package com.example.android.cdr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AddClasses extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int workloadHours = 0;
    private String grade = "EXC";

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

    public void addClass(View view)
    {
        String nameOfClass = getNameOfClass();

        if (nameOfClass != "")
        {
            if (workloadHours != 0)
            {
                makeToast(nameOfClass + " succesfully added!");
                return;
            } else
            {
                makeToast("Select a workload");
            }

            return;
        }
    }

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

    // Helper function

    public void makeToast(String text)
    {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
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
}

