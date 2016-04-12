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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class Invoerscherm extends AppCompatActivity {

    private Data data;
    private CourseAdapter courseAdapter;

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
            Log.e("invoer loading", e.toString());
        }
    }

    private Dialog createDetailsscherm(final Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.content_detailsscherm, null);
        final TextView vak = (TextView) view.findViewById(R.id.vak);
        final EditText grade = (EditText) view.findViewById(R.id.grade);
        final TextView ect = (TextView) view.findViewById(R.id.ect);
        final TextView period = (TextView) view.findViewById(R.id.period);

        vak.setText(course.getName());
        grade.setText(String.format(Locale.US, "%.1f", course.getGrade()));
        ect.setText(String.format("ECT %d", course.getEct()));
        period.setText(String.format("Period %d", course.getPeriod()));

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        course.setGrade(Double.parseDouble(grade.getText().toString()));
                        try {
                            data.save();
                        } catch (JSONException | IOException e) {
                            Log.e("invoer save", e.toString());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }



    private Dialog createNieuwVakScherm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.content_detailsscherm_nieuw_vak, null);
        final EditText vak = (EditText) view.findViewById(R.id.vak);
        final EditText grade = (EditText) view.findViewById(R.id.grade);
        final EditText ect = (EditText) view.findViewById(R.id.ect);
        final EditText period = (EditText) view.findViewById(R.id.period);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final Course course = new Course(vak.getText().toString(),
                                Short.parseShort(ect.getText().toString()),
                                Double.parseDouble(grade.getText().toString()),
                                Short.parseShort(period.getText().toString()));
                        course.isEdited();
                        data.add(course);
                        data.sortByPeriod();
                        courseAdapter.notifyDataSetChanged();
                        try {
                            data.save();
                        } catch (JSONException | IOException e) {
                            Log.e("invoer save", e.toString());
                        }
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
                createNieuwVakScherm().show();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        ListView courseList = (ListView) findViewById(R.id.grade_details);
        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Course course = Data.get(position);
                Dialog dialog = createDetailsscherm(course);
                dialog.show();
            }
        });

        data = new Data(this);
        courseAdapter = new CourseAdapter(this, Data.getCourses());
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
