package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;


public class MyReports extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        MyApplication m = ((MyApplication) getApplicationContext());
        ArrayList<Report> reports = m.reports_array;

        LinearLayout linear_holder = (LinearLayout) findViewById(R.id.linear_holder);
        WindowManager.LayoutParams LLParams =
                new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

        for(Report r : reports){
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setLayoutParams(LLParams);
            ImageView img = new ImageView(this);
            Log.d("MYREPORTS", r.getCapture().toString());
            img.setImageBitmap(r.getCapture());
            img.setMaxHeight(400);
            img.setAdjustViewBounds(true);
            img.setTop(10);
            img.setBottom(10);
            l.addView(img);
            LinearLayout internal = new LinearLayout(this);
            internal.setLayoutParams(LLParams);
            internal.setOrientation(LinearLayout.VERTICAL);
            TextView location = new TextView(this);
            location.setText("Location: " + r.getBuilding() + " - " + r.getArea());
            TextView issue = new TextView(this);
            issue.setText("Issue: " + r.getIssue_type());
            TextView description = new TextView(this);
            description.setText("Description: " + r.getDescription());
            internal.addView(location);
            internal.addView(issue);
            internal.addView(description);
            internal.setGravity(Gravity.CENTER);
            l.addView(internal);
            linear_holder.addView(l);
            Space space = new Space(this);
            space.setMinimumHeight(20);
            linear_holder.addView(space);
        }
    }

}
