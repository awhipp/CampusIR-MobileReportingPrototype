package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class FullReport extends Activity {

    MyApplication m;
    Context context;
    SharedPreferences prefs;
    private Handler uiHandler = new Handler();
    ProgressBar bar;
    TextView progress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_report);
        if(getActionBar()!=null) {
            getActionBar().setTitle("Detailed Report");
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Intent intent = getIntent();
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        final int index = intent.getIntExtra("ReportIndex", -1);
        m = ((MyApplication) getApplicationContext());
        ArrayList<Report> reports = m.reports_array;

        TextView header1 = (TextView) findViewById(R.id.header1);
        TextView header2 = (TextView) findViewById(R.id.header2);
        TextView header3 = (TextView) findViewById(R.id.header3);

        ImageView report_image = (ImageView) findViewById(R.id.report_image);

        final String description = reports.get(index).getDescription();
        String area = reports.get(index).getArea();
        String building = reports.get(index).getBuilding();
        String incident = reports.get(index).getIssue_type();
        Bitmap image = reports.get(index).getCapture();

        header1.setText(building + " " + area);
        header2.setText(incident);

        bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress = (TextView) findViewById(R.id.progress);
        setBar(10);
        new UpdateBar().execute(context);
        if(description.replaceAll(" ","").equals("")){
        header3.setText("");
        }else {
            header3.setText("\n" + description + "\n");
        }
        if(image != null){
            report_image.setImageBitmap(image);
        }else{
            header2.append("\n\n\n\n\n\n");
            report_image.setVisibility(View.INVISIBLE);
        }

        Button add_information = (Button) findViewById(R.id.add_button);
        Button delete_report = (Button) findViewById(R.id.delete_button);

        add_information.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(context);
                input.setTextColor(Color.BLACK);
                new AlertDialog.Builder(FullReport.this)
                        .setTitle("New Information")
                        .setView(input)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String inputText = input.getText().toString();
                                ((TextView) findViewById(R.id.header3)).append("\nNEW INFO. " + inputText);
                                m.reports_array.get(index).addDescription(inputText);
                                try {
                                    prefs.edit()
                                            .putString("REPORTS", byteToString(serialize(m.reports_array)))
                                            .apply();
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });

        delete_report.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(context);
                input.setTextColor(Color.BLACK);
                new AlertDialog.Builder(FullReport.this)
                        .setTitle("Delete Report")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                m.reports_array.remove(index);
                                try {
                                    prefs.edit()
                                            .putString("REPORTS", byteToString(serialize(m.reports_array)))
                                            .apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (m.reports_array.size() != 0) {
                                    Toast.makeText(context, "Report Deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MyReports.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(context, "No Reports Remaining", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MenuActivity.class);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


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

    public String byteToString(byte[] data){
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    private class UpdateBar extends AsyncTask<Context, Integer, Boolean> {

        protected Boolean doInBackground(Context... context) {
            new Timer().scheduleAtFixedRate(new TimerTask(){
                public void run() {
                    int progress = bar.getProgress();
                    if(progress == 10){
                        setBar(33);
                    }else if(progress == 33){
                        setBar(66);
                    }else if(progress == 66){
                        setBar(99);
                    }else if(progress == 99){
                        setBar(10);
                    }

                }
            }, 0, 1500);

            return true;
        }
    }

    private void setBar(final int amount){
        uiHandler.post(new Runnable(){
            public void run(){
                bar.setProgress(amount);
                if(amount == 10) {
                    progress.setText("Received");
                }else if(amount == 33) {
                    progress.setText("Evaluating");
                }else if(amount == 66) {
                    progress.setText("Repairing");
                }else if(amount == 99) {
                    progress.setText("Completed");
                }

            }
        });
    }


}
