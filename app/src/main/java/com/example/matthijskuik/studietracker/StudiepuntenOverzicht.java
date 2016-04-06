package com.example.matthijskuik.studietracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class StudiepuntenOverzicht extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studiepunten_overzicht);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
