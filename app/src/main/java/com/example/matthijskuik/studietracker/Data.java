package com.example.matthijskuik.studietracker;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Matthijs Kuik on 5-4-2016.
 */
public class Data {

    private Context context;
    private final String filename = "course_database";
    private static ArrayList<Course> courses = new ArrayList<>();

    public Data(Context context) {
        this.context = context;
    }

    public static ArrayList<Course> getCourses() {
        return courses;
    }

    public static short[] getPeriodEctScores() {
        short[] etcs = new short[getMaxPeriod() + 1];
        for (final Course course : courses) {
            etcs[course.getPeriod()] += course.getEct();
        }
        return etcs;
    }

    public static short getSumEct() {
        short sum = 0;
        for (final Course course : courses) if (course.isAPassingGrade()) sum += course.getEct();
        return sum;
    }

    public static short getMaxEct() {
        short max = Short.MIN_VALUE;
        for (Course course : courses) if (course.getEct() > max) max = course.getEct();
        return max;
    }

    public static short getMaxPeriod() {
        short max = Short.MIN_VALUE;
        for (Course course : courses) if (course.getPeriod() > max) max = course.getPeriod();
        return max;
    }

    public static void sortByPeriod() {
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course course1, Course course2) {
                if (course1.getPeriod() < course2.getPeriod()) {
                    return -1;
                } else if (course1.getPeriod() == course2.getPeriod()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    public static Course get(int i) {
        return courses.get(i);
    }

    public static int size() {
        return courses.size();
    }

    public static void add(final Course course) {
        courses.add(course);
    }

    public void load() throws JSONException, IOException {
        JSONArray array;
        try {
            FileInputStream fis = context.openFileInput(filename);
            array = new JSONArray(convertStreamToString(fis));
            fis.close();
        } catch (FileNotFoundException e) {
            array = new JSONArray(context.getString(R.string.grades));
        }
        if (array.length() > 0) courses.clear();
        for (int i = 0; i != array.length(); ++i) {
            courses.add(new Course(array.getJSONObject(i)));
        }
    }

    public void save() throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (final Course course : courses) array.put(course.toJSON());
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        fos.write(array.toString().getBytes());
        fos.close();
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
