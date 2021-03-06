package com.example.android.cdr;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Users extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ClassesData classesDB = new ClassesData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.users, menu);
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

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addUser(View view)
    {
        String user = getNameOfUser();

        if (! user.equals(""))
        {
            classesDB.addUser(user);
            makeToast("User successfully added");

            printAllUsers();
        } else
        {
            makeToast("Invalid name");
        }
    }

    private void printAllUsers()
    {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.registeredUsers);

        Cursor cursor = classesDB.getAllUsersData();

        try {
            while (cursor.moveToNext()) {
                String nameOfUser =
                        cursor.getString(cursor.getColumnIndex(ClassesData.COLUMN_NAME));

                int id = cursor.getInt(cursor.getColumnIndex(ClassesData.COLUMN_ID));

                int workload = cursor.getInt(cursor.getColumnIndex(ClassesData.COLUMN_WORKLOAD));

                int points = cursor.getInt(cursor.getColumnIndex(ClassesData.COLUMN_POINTS));

                printUser(nameOfUser, id, workload, points, mainLayout);
            }
        } finally {
            cursor.close();
        }
    }

    private void printUser(String name, int id, int workload, int points, LinearLayout layout)
    {
        TextView user = new TextView(this);

        user.setText(Integer.toString(id) + name + "\t" + Integer.toString(workload)
        + "\t" + Integer.toString(points));
        user.setTextSize(16);

        layout.addView(user);
    }

    private String getNameOfUser()
    {
        EditText input = (EditText) findViewById(R.id.nameOfUser);

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

    public void makeToast(String text)
    {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
