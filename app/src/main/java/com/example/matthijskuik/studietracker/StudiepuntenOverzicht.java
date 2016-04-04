package com.example.matthijskuik.studietracker;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudiepuntenOverzicht extends AppCompatActivity {

    private GraphView graph;
    private ListView courseList;
    private CourseAdapter courseAdapter;
    private ArrayList<Course> courseData;
    private short[] etcs;
    private TextView ectScore;
    private TextView advice;
    private TextView name;
    private TextView period;

    public void addCourse(final Course course) {
        courseData.add(course);
        etcs[course.period - 1] += course.ect;
//        courseAdapter.add(course);
    }

    public short getSumEct() {
        short sum = 0;
        for (short i : etcs) sum += i;
        return sum;
    }

    public void notifyEctChange() {
        final short score = getSumEct();
        ectScore.setText(String.format("%d ects", score));
        if (score <= 40) {
            advice.setText("BSA");
        } else if (score <= 50) {
            advice.setText("Blijft zitten");
        } else {
            advice.setText("Goed bezig!");
        }
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
        graph = (GraphView) findViewById(R.id.grade_graph);
        courseList = (ListView) findViewById(R.id.grade_details);
        ectScore = (TextView) findViewById(R.id.total_ect);
        advice = (TextView) findViewById(R.id.advice);
        name = (EditText) findViewById(R.id.name);
        period = (TextView) findViewById(R.id.period);

        courseData = new ArrayList<>();
        etcs = new short[4];

        try {
            JSONArray jsonArray = new JSONArray(getString(R.string.grades));
            for (int i = 0; i != jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.getJSONObject(i);

//                Log.i("json" , jsonArray.toString());

                final Course course = new Course(obj.getString("name"),
                        Short.parseShort(obj.getString("ects")),
                        Double.parseDouble(obj.getString("grade")),
                        Short.parseShort(obj.getString("period")));
                addCourse(course);

                Log.i("course " + i, course.toString());
                Log.i("item", obj.toString());
            }
        } catch (JSONException e) {
            Log.e("grade rescourse", e.toString());
        }

        courseAdapter  = new CourseAdapter(this, courseData);
        courseList.setAdapter(courseAdapter);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, etcs[0]),
                new DataPoint(2, etcs[1]),
                new DataPoint(3, etcs[2]),
                new DataPoint(4, etcs[3])
        });
        series.setSpacing(5);
        series.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        graph.addSeries(series);

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(6);
        graph.getGridLabelRenderer().setNumVerticalLabels(3);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        short max = Short.MIN_VALUE;
        for (short i : etcs) if (i > max) max = i;
        graph.getViewport().setMaxY(max);

        notifyEctChange();
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
