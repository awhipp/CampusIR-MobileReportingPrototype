package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyReports extends Activity {

    Context context;
    MyApplication m;
    SharedPreferences prefs;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if(getActionBar()!=null) {
            getActionBar().setTitle("My Reports");
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        m = ((MyApplication) getApplicationContext());
        ArrayList<Report> reports = m.reports_array;

        LinearLayout linear_holder = (LinearLayout) findViewById(R.id.linear_holder);
        WindowManager.LayoutParams LLParams =
                new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

        TextView title = (TextView) findViewById(R.id.title);
        if(reports.size() == 0){
            Toast.makeText(this, "No Reports Submitted", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            title.setText("Tap to View Details\n");
        }

        /*
        linear_holder - overall layout for the entire activity
        report - overall individual report
        internal - text portion
         */
        for(int i = 0; i < reports.size(); i++){
            final int j = i;
            final Report r = reports.get(i);
            ImageView img = new ImageView(this);
            Bitmap bitmap = r.getCapture();
            if(bitmap == null){
                img.setImageDrawable(getResources().getDrawable(R.drawable.noimage));
            }else {
                img.setImageBitmap(bitmap);
            }
            img.setMaxWidth(250);
            img.setAdjustViewBounds(true);
            img.setTop(10);
            img.setBottom(10);
            img.setBackground(getResources().getDrawable(R.drawable.border));

            LinearLayout internal = new LinearLayout(this);
            internal.setLayoutParams(LLParams);
            internal.setOrientation(LinearLayout.VERTICAL);
            internal.setGravity(Gravity.CENTER);

            TextView location = new TextView(this);
            location.setText("Location: " + r.getBuilding());

            TextView area = new TextView(this);
            area.setText("Room/Area: " + r.getArea());

            TextView issue = new TextView(this);
            issue.setText("Issue: " + r.getIssue_type());

            internal.addView(location);
            internal.addView(area);
            internal.addView(issue);

            Space internal_spacer = new Space(this);
            internal_spacer.setMinimumWidth(20);

//            ImageView chevron = new ImageView(this);
//            chevron.setImageDrawable(getResources().getDrawable(R.drawable.chevron));
//            chevron.setMaxWidth(125);
//            chevron.setAdjustViewBounds(true);

            LinearLayout report = new LinearLayout(this);
            report.setPadding(0,20,0,20);
            report.setOrientation(LinearLayout.HORIZONTAL);
            report.setGravity(Gravity.CENTER);
            report.setLayoutParams(LLParams);
            report.addView(img);
            report.addView(internal_spacer);
            report.addView(internal);
//            report.addView(chevron);

            report.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullReport.class);
                    intent.putExtra("ReportIndex", j);
                    startActivity(intent);
                }
            });

            linear_holder.addView(report);
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
