package com.example.android.cdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.TreeSet;

public class AddClass extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private ClassesData classesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setSpinner();

        // this.deleteDatabase(ClassesData.DATABASE_NAME);

        // Initiate database
        classesDB = new ClassesData(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_class, menu);

        /* addClasses = menu.findItem(R.id.nav_add_classes);

        addClasses.setChecked(true); */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_classes) {
            // Handle the camera action
        } else if (id == R.id.nav_show_classes) {
            changeActivity();
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, Users.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private TreeSet<Integer> allSemesters = new TreeSet<>();

    private int workloadHours = 0;
    private String grade = "EXC";

    // Constants
    private static final int MAX_SEMESTER = 16;

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
        /*
        An item was selected. You can retrieve the selected item using
        parent.getItemAtPosition(pos)
        */
        grade = parent.getItemAtPosition(pos).toString();
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
        if (! nameOfClass.equals(""))
        {
            // Check to see if the class isn't already registered
            if (!classesDB.isClassRegistered(nameOfClass))
            {
                if (workloadHours != 0)
                {
                    int semester = getSemester();

                    if (semester > 0)
                    {
                        allSemesters.add(semester);

                        double nota = gradeToNota(grade);

                        classesDB.addClass(nameOfClass, semester, workloadHours, nota);

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

    private void changeActivity()
    {
        Intent intent = new Intent(this, ShowClasses.class);

        intent.putExtra("Semesters", allSemesters);

        startActivity(intent);
    }
}
