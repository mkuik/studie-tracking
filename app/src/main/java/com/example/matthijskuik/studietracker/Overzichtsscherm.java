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

    private ECTPointsBar period1bar;
    private ECTPointsBar period2bar;
    private ECTPointsBar period3bar;
    private ECTPointsBar period4bar;
    private TextView successTextView;
    private TextView failedTextView;
    private TextView unknownTextView;
    private Data data;

    public void setupGraphData() {
        final short maxPeriod = Data.getMaxPeriod();
        final short[] success = new short[maxPeriod + 1];
        final short[] failed = new short[maxPeriod + 1];
        final short[] unknown = new short[maxPeriod + 1];
        final short period = Course.getCurrentPeriod();

        int sumSuccess = 0;
        int sumFailed = 0;
        int sumUnknown = 0;
        for (final Course course : Data.getCourses()) {
            if (course.isAPassingGrade()) {
                success[course.getPeriod()] += course.getEct();
                sumSuccess += course.getEct();
            } else if (period > course.getPeriod()) {
                failed[course.getPeriod()] += course.getEct();
                sumFailed += course.getEct();
            } else {
                unknown[course.getPeriod()] += course.getEct();
                sumUnknown += course.getEct();
            }
        }

        setECTBarValues(period1bar, success[1], failed[1], unknown[1]);
        setECTBarValues(period2bar, success[2], failed[2], unknown[2]);
        setECTBarValues(period3bar, success[3], failed[3], unknown[3]);
        setECTBarValues(period4bar, success[4], failed[4], unknown[4]);

        successTextView.setText(String.format("%d", sumSuccess));
        failedTextView.setText(String.format("%d", sumFailed));
        unknownTextView.setText(String.format("%d", sumUnknown));
    }

    private void setECTBarValues(ECTPointsBar bar, final int success, final int failed, final int unknown) {
        bar.setSuccessValue(success);
        bar.setFailedValue(failed);
        bar.setUnknownValue(unknown);
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

        period1bar = (ECTPointsBar) findViewById(R.id.period_1_ect_bar);
        period2bar = (ECTPointsBar) findViewById(R.id.period_2_ect_bar);
        period3bar = (ECTPointsBar) findViewById(R.id.period_3_ect_bar);
        period4bar = (ECTPointsBar) findViewById(R.id.period_4_ect_bar);

        successTextView = (TextView) findViewById(R.id.success_score);
        failedTextView = (TextView) findViewById(R.id.failed_score);
        unknownTextView = (TextView) findViewById(R.id.unknown_score);

        data = new Data(this);
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
