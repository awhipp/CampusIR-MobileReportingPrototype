package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;


public class MenuActivity extends Activity {

    Context context;
    SharedPreferences prefs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Button my_reports = (Button) findViewById(R.id.my_reports);
        Button new_report = (Button) findViewById(R.id.new_report);
        Button logout = (Button) findViewById(R.id.logout_button);

        my_reports.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MyReports.class);
                startActivity(intent);
            }
        });

        new_report.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, NewReport.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                prefs.edit().putBoolean("REMEMBER", false).apply();
                finish();
            }
        });
    }

    protected void onResume(){super.onResume();}
    protected void onStop(){super.onStop();}
}
