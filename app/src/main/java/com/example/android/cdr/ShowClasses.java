package com.example.android.cdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowClasses extends AppCompatActivity {

    private HashMap<String, ArrayList> classes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_classes);

        Intent intent = getIntent();

        classes = (HashMap) intent.getSerializableExtra("Classes");

        printClasses();
    }

    private void printClasses()
    {
        if (classes.isEmpty())
        {
            printWarning();
        } else
        {
            for (String nameOfClass : classes.keySet())
            {
                TextView textView = new TextView(this);

                String text = nameOfClass + " | " + getGrade(nameOfClass) + " | " +
                        getWorkload(nameOfClass) + " | " + getSemester(nameOfClass);

                textView.setText(text);
                textView.setTextSize(16);

                //layout.addView(textView);

                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.showClassesMain);

                mainLayout.addView(textView);
            }
        }
    }

    private void printWarning()
    {
        TextView warning = new TextView(this);

        warning.setText("No classes were registered!");
        warning.setTextSize(16);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.showClassesMain);

        mainLayout.addView(warning);
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
