package com.example.matthijskuik.studietracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Invoerscherm extends AppCompatActivity {

    private ArrayList<Course> courseData;
    private Data data;

    public void addCourse(final Course course) {
        courseData.add(course);
    }

    public void loadData() {
        courseData.clear();
        try {
            final JSONArray courses = data.getCourseNames();
            Log.i("load courses", courses.toString());
            for (int i = 0; i != courses.length(); ++i) {
                Course course = data.getCourse(courses.getString(i));
                addCourse(course);
            }
        } catch (FileNotFoundException e1) {
            Log.i("load from init", e1.toString());
            try {
                JSONArray courses = new JSONArray(getString(R.string.grades));
                for (int i = 0; i != courses.length(); ++i) {
                    Course course = new Course(courses.getJSONObject(i));
                    addCourse(course);
                    course.setEdited(true);
                }
            } catch (JSONException e2) {
                Log.e("load from init", e2.toString());
            }
        } catch (JSONException | IOException e) {
            Log.e("load from save", e.toString());
        }
    }

    public void saveData() {
        JSONArray courses = new JSONArray();
        for (final Course course : courseData) {
            courses.put(course.getName());
        }
        Log.i("save courses", courses.toString());
        try {
            data.setCourseNames(courses);
            for (int i = 0; i != courses.length(); ++i) {
                final Course course = courseData.get(i);
                if (course.isEdited()) data.setCourse(course);
            }
        } catch (IOException | JSONException e) {
            Log.e("save courses", e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private Dialog createDetailsscherm(final Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.content_detailsscherm, null);
        TextView vak = (TextView) view.findViewById(R.id.vak);
        TextView grade = (TextView) view.findViewById(R.id.grade);
        TextView ect = (TextView) view.findViewById(R.id.ect);
        TextView period = (TextView) view.findViewById(R.id.period);

        vak.setText(course.getName());
        grade.setText(String.format("%.1f", course.getGrade()));
        ect.setText(String.format("ECT %d", course.getEct()));
        period.setText(String.format("Period %d", course.getPeriod()));

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoerscherm);
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
        ListView courseList = (ListView) findViewById(R.id.grade_details);
        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Course course = courseData.get(position);
                Dialog dialog = createDetailsscherm(course);
                dialog.show();
            }
        });

        data = new Data(this);
        courseData = new ArrayList<>();
        CourseAdapter courseAdapter = new CourseAdapter(this, courseData);
        courseList.setAdapter(courseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_studiepunten_overzicht, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.action_hoofdscherm:
                this.startActivity(new Intent(this, Hoofdscherm.class));
                return true;
            case R.id.action_invoerscherm:
                this.startActivity(new Intent(this, Invoerscherm.class));
                return true;
            case R.id.action_overzichtsscherm:
                this.startActivity(new Intent(this, Overzichtsscherm.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
