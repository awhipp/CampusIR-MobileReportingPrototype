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

        TextView header1 = (TextView) findViewById(R.id.header1);
        TextView header2 = (TextView) findViewById(R.id.header2);
        TextView header3 = (TextView) findViewById(R.id.header3);

        ImageView report_image = (ImageView) findViewById(R.id.report_image);

        String description = reports.get(index).getDescription();
        String area = reports.get(index).getArea();
        String building = reports.get(index).getBuilding();
        String incident = reports.get(index).getIssue_type();
        Bitmap image = reports.get(index).getCapture();

        header1.setText("Building: " + building);
        header2.setText("Room/Area: " + area + "\nIssue: " + incident);
        header3.setText("More Information: " + description);

        if(image == null){
            report_image.setImageDrawable(getResources().getDrawable(R.drawable.noimage));
        }else{
            report_image.setImageBitmap(image);
        }
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
