package com.example.matthijskuik.studietracker;

import android.content.Context;

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

/**
 * Created by Matthijs Kuik on 5-4-2016.
 */
public class Data {

    private Context context;
    private final String metaFilename = "meta";

    public Data(Context context) {
        this.context = context;
    }

    public Course getCourse(final String name) throws IOException, JSONException {
        FileInputStream fis = context.openFileInput(name);
        final JSONObject obj = new JSONObject(convertStreamToString(fis));
        fis.close();
        return new Course(obj);
    }

    public void setCourse(final Course course) throws IOException, JSONException {
        FileOutputStream fos = context.openFileOutput(course.getName(), Context.MODE_PRIVATE);
        fos.write(course.toJSON().toString().getBytes());
        fos.close();
    }

    public JSONArray getCourseNames() throws IOException, JSONException {
        FileInputStream fis = context.openFileInput(metaFilename);
        return new JSONArray(convertStreamToString(fis));
    }

    public void setCourseNames(final JSONArray jsonArray) throws IOException {
        FileOutputStream fos = context.openFileOutput(metaFilename, Context.MODE_PRIVATE);
        fos.write(jsonArray.toString().getBytes());
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
