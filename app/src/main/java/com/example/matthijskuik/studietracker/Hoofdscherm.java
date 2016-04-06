package com.example.matthijskuik.studietracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
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

public class Hoofdscherm extends AppCompatActivity {

    private GraphView graph;
    private ArrayList<Course> courseData;
    private TextView ectScore;
    private TextView advice;
    private TextView name;
    private TextView period;
    private Data data;

    public void addCourse(final Course course) {
        courseData.add(course);
    }

    public short[] getPeriodEctScores() {
        short[] etcs = new short[4];
        for (final Course course : courseData) etcs[course.getPeriod() - 1] += course.getEct();
        return etcs;
    }

    public short getSumEct() {
        short sum = 0;
        for (final Course course : courseData) sum += course.getEct();
        return sum;
    }

    public void setupSumEct() {
        final short score = getSumEct();
        ectScore.setText(String.format("%d punten", score));
        if (score <= 40) {
            advice.setText("BSA");
        } else if (score <= 50) {
            advice.setText("Blijft zitten");
        } else {
            advice.setText("Goed bezig!");
        }
    }

    public void setupPeriod() {
        Calendar c = Calendar.getInstance();
        final int week = c.get(Calendar.WEEK_OF_YEAR);
        short i = 0;
        if (week >= 35 && week <= 46) {
            i = 1;
        } else if((week >= 47 && week <= 53) || week <= 5) {
            i = 2;
        } else if(week <= 16) {
            i = 3;
        } else if(week <= 28) {
            i = 4;
        } else {
            i = 5;
        }
        period.setText(String.format("Periode %d", i));
    }

    public void setupGraphData() {
        graph.removeAllSeries();
        final short[] etcs = getPeriodEctScores();

        graph.getViewport().setMaxX(courseData.size() + 1);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        for (int i = 0; i != courseData.size(); ++i) {
            DataPoint dataPoint = new DataPoint(i + 1, courseData.get(i).getEct());
            series.appendData(dataPoint, false, courseData.size() + 1);

            Log.i("series append", courseData.get(i).toString());
        }
        series.setSpacing(6);
        series.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        graph.addSeries(series);

        // Adapt graph y bounds to values
        short max = Short.MIN_VALUE;
        for (Course course : courseData) if (course.getEct() > max) max = course.getEct();
        graph.getViewport().setMaxY(max);
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
        setupSumEct();
        setupPeriod();
        setupGraphData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoofdscherm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        graph = (GraphView) findViewById(R.id.course_overview);
        ectScore = (TextView) findViewById(R.id.total_ect);
        advice = (TextView) findViewById(R.id.advice);
        name = (EditText) findViewById(R.id.name);
        period = (TextView) findViewById(R.id.period);

        data = new Data(this);
        courseData = new ArrayList<>();

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setNumHorizontalLabels(0);
        graph.getGridLabelRenderer().setNumVerticalLabels(0);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
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