package com.rzepka.hsiao.whipp.campusir;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    String building;
    String area;
    String issue_type;
    String description;
    byte[] capture;

    public Report(String building, String area, String issue_type, String description, Bitmap capture){
        this.building = building;
        this.area = area;
        this.issue_type = issue_type;
        this.description = description;
        /* So that we can serialize the Report into a SharedPreference */
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        capture.compress(Bitmap.CompressFormat.JPEG, 50, blob);
        this.capture = blob.toByteArray();
    }

    public String getBuilding() { return this.building; }

    public String getArea() { return this.area; }

    public String getIssue_type() { return this.issue_type; }

    public String getDescription() { return this.description; }

    public Bitmap getCapture() { return BitmapFactory.decodeByteArray(capture , 0, capture.length);}

}
