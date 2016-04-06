package com.example.matthijskuik.studietracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Overzichtsscherm extends AppCompatActivity {

    private GraphView graph;
    private ArrayList<Course> courseData;
    private Data data;

    public void addCourse(final Course course) {
        courseData.add(course);
    }

    public short[] getPeriodEctScores() {
        short[] etcs = new short[4];
        for (final Course course : courseData) etcs[course.getPeriod() - 1] += course.getEct();
        return etcs;
    }

    public void setupGraphData() {
        graph.removeAllSeries();
        final short[] etcs = getPeriodEctScores();

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        for (int i = 0; i != etcs.length; ++i) {
            DataPoint dataPoint = new DataPoint(i + 1, etcs[i]);
            series.appendData(dataPoint, false, etcs.length + 1);
        }
        series.setSpacing(6);
        series.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        graph.addSeries(series);
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
        setupGraphData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overzichtscherm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        graph = (GraphView) findViewById(R.id.course_overview);

        data = new Data(this);
        courseData = new ArrayList<>();

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(6);
        graph.getGridLabelRenderer().setNumVerticalLabels(21);

        graph.getViewport().setMaxX(5);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(20);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
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
