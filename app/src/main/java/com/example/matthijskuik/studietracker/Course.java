package com.example.matthijskuik.studietracker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matthijs Kuik on 31-3-2016.
 */
public class Course {
    public String name;
    public short ect;
    public double grade;
    public short period;

    Course(final String name, final short ect, final double grade, final short period) {
        this.name = name;
        this.ect = ect;
        this.grade = grade;
        this.period = period;
    }

    Course(final JSONObject obj) throws JSONException {
        this(obj.getString("name"),
                Short.parseShort(obj.getString("ects")),
                Double.parseDouble(obj.getString("grade")),
                Short.parseShort(obj.getString("period")));
    }

    public String toString() {
        return String.format("%s etc:%d grade:%f period%d", name, ect, grade, period);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("ects", "" + ect);
        json.put("grade", "" + grade);
        json.put("period", "" + period);
        return json;
    }
}
