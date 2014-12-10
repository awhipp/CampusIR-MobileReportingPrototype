package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

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

        Button clear_cache = (Button) findViewById(R.id.clear_cache);

        clear_cache.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m.reports_array = new ArrayList<>();
                boolean remember = prefs.getBoolean("REMEMBER", false);
                prefs.edit().clear().apply();
                prefs.edit().putBoolean("REMEMBER",remember);
                Intent intent = new Intent(context, MyReports.class);
                startActivity(intent);
                finish();
            }
        });

        for(Report r : reports){
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setLayoutParams(LLParams);
            ImageView img = new ImageView(this);
            img.setImageBitmap(r.getCapture());
            img.setMaxWidth(250);
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
            Space internal_spacer = new Space(this);
            internal_spacer.setMinimumWidth(20);
            l.addView(internal_spacer);
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
