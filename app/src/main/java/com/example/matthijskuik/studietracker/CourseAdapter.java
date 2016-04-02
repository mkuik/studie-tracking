package com.example.matthijskuik.studietracker;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matthijs Kuik on 31-3-2016.
 */

// TODO gebruik een andere baseadapter subclass

public class CourseAdapter extends BaseAdapter {

    private ArrayList<Course> data;
    private Context context;

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public CourseAdapter(Context context, ArrayList<Course> items) {
        this.data = items;
        this.context = context;
    }

    public View getView(int position, View v, ViewGroup parent) {

        if (v == null) {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.course_list_item, parent, false);
        }

        TextView name = (TextView) v.findViewById(R.id.name);
        TextView details = (TextView) v.findViewById(R.id.details);
        TextView grade = (TextView) v.findViewById(R.id.grade);
        final Course course = (Course) getItem(position);
        name.setText(course.name);
        details.setText("ect:" + String.valueOf(course.ect) + ", period:" + String.valueOf(course.period));
        grade.setText(String.valueOf(course.grade));

        Log.i("view " + position + "/" + getCount(), ((Course) getItem(position)).toString());

        return v;
    }
}