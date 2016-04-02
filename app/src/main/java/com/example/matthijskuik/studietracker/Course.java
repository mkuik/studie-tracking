package com.example.matthijskuik.studietracker;

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

    public String toString() {
        return String.format("%s etc:%d grade:%f period%d", name, ect, grade, period);
    }
}
