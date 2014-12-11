package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class FullReport extends Activity {

    MyApplication m;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_report);
        if(getActionBar()!=null) {
            getActionBar().setTitle("Detailed Report");
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        int index = intent.getIntExtra("ReportIndex", -1);
        m = ((MyApplication) getApplicationContext());
        ArrayList<Report> reports = m.reports_array;

        String description = reports.get(index).getDescription();
        String area = reports.get(index).getArea();
        String building = reports.get(index).getBuilding();
        String incident = reports.get(index).getIssue_type();
        Bitmap image = reports.get(index).getCapture();
        ((TextView) findViewById(R.id.test)).setText(description + "\n" + area + "\n" + building + "\n" + incident);
        ((ImageView) findViewById(R.id.test2)).setImageBitmap(image);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
