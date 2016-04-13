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
import com.jjoe64.graphview.ValueDependentColor;
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
    private TextView ectScore;
    private TextView advice;
    private TextView name;
    private TextView period;
    private Data data;

    public void setupSumEct() {
        short success = 0;
        short todo = 0;
        final short period = Course.getCurrentPeriod();
        for (final Course course : Data.getCourses()) {
            if (course.isAPassingGrade()) {
                success += course.getEct();
            } else if (period <= course.getPeriod()) {
                todo += course.getEct();
            }
        }
        final int score = success + todo;
        ectScore.setText(String.format("%d punten", success));
        if (score <= 40) {
            advice.setText(R.string.bsa_warning);
        } else if (score <= 50) {
            advice.setText(R.string.blijft_zitten_warning);
        } else {
            advice.setText(R.string.alles_gehaald_warning);
        }
    }

    public void setupPeriod() {
        period.setText(String.format("Periode %d", Course.getCurrentPeriod()));
    }

    public void setupGraphData() {
        graph.removeAllSeries();

        graph.getViewport().setMaxX(Data.size() + 1);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        for (int i = 0; i != Data.size(); ++i) {
            final Course course = Data.get(i);
            DataPoint dataPoint = new DataPoint(i + 1, course.getEct());
            series.appendData(dataPoint, false, Data.size() + 1);
//            Log.i("series append", courseData.get(i).toString());
        }
        final short period = Course.getCurrentPeriod();
        final int passColor = ContextCompat.getColor(this, R.color.colorAccent);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                final Course course = Data.get((int) data.getX() - 1);
                if (course.isAPassingGrade()) {
                    return passColor;
                } else if (period > course.getPeriod()) {
                    return Color.RED;
                } else {
                    return Color.GRAY;
                }
            }
        });
        series.setSpacing(6);
        graph.addSeries(series);

        // Adapt graph y bounds to values
        graph.getViewport().setMaxY(Data.getMaxEct());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            data.load();
        } catch (JSONException | IOException e) {
            Log.i("hoofd loading", e.toString());
        }
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
