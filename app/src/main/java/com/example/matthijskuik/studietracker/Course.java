package com.example.matthijskuik.studietracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Matthijs Kuik on 31-3-2016.
 */
public class Course {
    private String name;
    private short ect;
    private double grade;
    private short period;
    private boolean edited = false;

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

    public boolean isAPassingGrade() {
        return grade >= 5.5;
    }

    public static short getCurrentPeriod() {
        Calendar c = Calendar.getInstance();
        final int week = c.get(Calendar.WEEK_OF_YEAR);
        short i;
        if (week >= 35 && week <= 46) {
            i = 1;
        } else if((week >= 47 && week <= 53) || week <= 5) {
            i = 2;
        } else if(week <= 16) {
            i = 3;
        } else if(week <= 28) {
            i = 4;
        } else {
            i = 5;
        }
        return i;
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

    public short getEct() {
        return ect;
    }

    public void setEct(short ect) {
        if (this.ect != ect) edited = true;
        this.ect = ect;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        if (this.grade != grade) edited = true;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!this.name.equals(name)) edited = true;
        this.name = name;
    }

    public short getPeriod() {
        return period;
    }

    public void setPeriod(short period) {
        if (this.period != period) edited = true;
        this.period = period;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
