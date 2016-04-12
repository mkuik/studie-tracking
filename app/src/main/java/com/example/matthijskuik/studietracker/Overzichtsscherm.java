package com.example.matthijskuik.studietracker;

import android.content.Intent;
import android.graphics.Color;
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
    private Data data;

    public void setupGraphData() {
        graph.removeAllSeries();

        final short maxPeriod = Data.getMaxPeriod();
        final short[] succes = new short[maxPeriod + 1];
        final short[] failed = new short[maxPeriod + 1];
        final short period = Course.getCurrentPeriod();
        for (final Course course : Data.getCourses()) {
            if (course.isAPassingGrade()) {
                succes[course.getPeriod()] += course.getEct();
            } else if (period > course.getPeriod()) {
                failed[course.getPeriod()] += course.getEct();
            }
        }

        BarGraphSeries<DataPoint> succesSeries = new BarGraphSeries<>();
        BarGraphSeries<DataPoint> failedSeries = new BarGraphSeries<>();
        for (int i = 0; i <= maxPeriod; ++i) {
            succesSeries.appendData(new DataPoint(i, succes[i]), false, succes.length);
            failedSeries.appendData(new DataPoint(i, failed[i]), false, failed.length);
        }
        succesSeries.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        failedSeries.setColor(Color.RED);

        graph.addSeries(succesSeries);
        graph.addSeries(failedSeries);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            data.load();
        } catch (JSONException | IOException e) {
            Log.e("overzicht loading", e.toString());
        }
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
