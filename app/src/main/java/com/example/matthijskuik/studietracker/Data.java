package com.example.matthijskuik.studietracker;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Matthijs Kuik on 5-4-2016.
 */
public class Data {

    private Context context;

    public Data(Context context) {
        this.context = context;
    }

    public Course getCourse(final String name) throws IOException, JSONException {
        FileInputStream fis = context.openFileInput(name);
        final JSONObject obj = new JSONObject(convertStreamToString(fis));
        final Course course = new Course(obj.getString("name"),
                Short.parseShort(obj.getString("ects")),
                Double.parseDouble(obj.getString("grade")),
                Short.parseShort(obj.getString("period")));
        return course;
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
